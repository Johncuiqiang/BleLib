package ling.ai.lingblekit.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import java.util.LinkedList;
import java.util.List;
import ling.ai.lingblekit.ble.BleGattHelper;
import static ling.ai.lingblekit.constant.BleStatusConstant.STATE_CONNECTED;
import static ling.ai.lingblekit.constant.BleStatusConstant.STATE_CONNECTING;
import static ling.ai.lingblekit.constant.BleStatusConstant.STATE_DISCONNECTED;


/**
 * Created by cuiqiang on 18-1-4.
 *
 * @author cuiqiang
 */

public class ClientGattTool {

    private final static String TAG = "ble";

    private int mConnectionState = STATE_DISCONNECTED;

    private Context mContext;
    private BleGattHelper mBleGattHelper;//蓝牙辅助类
    private BluetoothGatt mBluetoothGatt;//gatt协议ble对象
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配者
    private List<IBleClientConnectCallback> mIBleClientConnectCallbackList = new LinkedList<>();
    private List<IBleClientCallback> mIBleClientCallbackList = new LinkedList<>();

    public ClientGattTool(Context context) {
        this.mContext = context;
        this.mBleGattHelper = new BleGattHelper(mContext);
        boolean isEnable = mBleGattHelper.initBle();
        if (isEnable) {
            mBluetoothAdapter = mBleGattHelper.getBluetoothAdapter();
        }
    }

    /**
     * 打开蓝牙
     */
    public void openBle() {
        mBleGattHelper.openBle();
    }

    /**
     * 连接蓝牙
     *
     * @param address 蓝牙地址
     * @return true连接成功 false连接失败
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }
        //根据地址连接远程设备
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        //连接已开启的GATT服务
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        setConnectionStatus(STATE_CONNECTING);
        return true;
    }

    /**
     * 搜索服务
     */
    public void discoverServices() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.discoverServices();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 释放的方法
     */
    public void release() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 读取属性
     *
     * @param characteristic 特征
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * 发通知
     *
     * @param characteristic 特征
     * @param enabled        true 通知可用 false通知不可用
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                 boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return false;
        }
        boolean isEnableNotice = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        writeDescriptors(characteristic,isEnableNotice);
        return isEnableNotice;
    }

    private void writeDescriptors(BluetoothGattCharacteristic characteristic, boolean isEnableNotice){
        if (isEnableNotice) {
            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
            if (null != descriptors && descriptors.size() > 0) {
                for (BluetoothGattDescriptor descriptor : descriptors) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
    }

    /**
     * 得到所有服务
     *
     * @return 所有服务
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }


    /**
     * 写入属性
     *
     * @param characteristic 特征
     * @return true写入成功 false写入失败
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return false;
        }
        SystemClock.sleep(300);
        characteristic.setValue(bytes);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        //不需要外围设备响应,持续写入
        //setMtu(50);有些机型设置无效,华为手机可以,设备目前设置会有不能通信的问题
        boolean isSendSuccess = mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i(TAG ,"ble isSendSuccess "+isSendSuccess);
        return isSendSuccess;
    }

    /**
     * 设置一次最大传输多少字节
     *
     * @param size
     */
    public void setMtu(int size) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothGatt.requestMtu(size);
        }
    }

    //判断特征可读
    public boolean ifCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0);
    }

    //判断特征可写
    public boolean ifCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0);
    }

    //判断特征是否具备通知属性
    public boolean ifCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0 ||
                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0);
    }


    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * 连接状态的改变
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                discoverServices();
                setConnectionStatus(STATE_CONNECTED);
                for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
                    iBleClientCallback.onConnectionStateChange(gatt, mConnectionState);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                setConnectionStatus(STATE_DISCONNECTED);
                for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
                    iBleClientCallback.onConnectionStateChange(gatt, mConnectionState);
                }
            }
        }

        /**
         * 发现服务
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG ,"onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                if (null != services && services.size() > 0){
                    for (BluetoothGattService service : services) {
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        if(null!=characteristics && characteristics.size()>0){
                            for (BluetoothGattCharacteristic characteristic : characteristics) {
                                setCharacteristicNotification(characteristic,true);
                            }
                        }
                    }
                }
                for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
                    iBleClientCallback.onServicesDiscovered(gatt);
                }
            } else {
                excuteError(status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.i(TAG , "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
                    iBleClientCallback.onCharacteristicRead(gatt, characteristic);
                }
            } else {
                excuteError(status);
            }
        }

        /**
         * 接收外设的消息
         * @param gatt           BluetoothProfile的对象
         * @param characteristic 特征
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG , "onCharacteristicChanged");
            for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
                iBleClientCallback.onCharacteristicChanged(gatt, characteristic);
            }
        }

        //通知写入的回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            Log.i(TAG , "onDescriptorWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] value = descriptor.getValue();
            }
        }

        //写操作的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                          int status) {
            Log.i(TAG , "onCharacteristicWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] value = characteristic.getValue();

            }
        }

    };

    /**
     * 设置连接蓝牙状态
     *
     * @param status
     */
    private void setConnectionStatus(int status) {
        mConnectionState = status;
        for (IBleClientConnectCallback iBleClientConnectCallback : mIBleClientConnectCallbackList) {
            iBleClientConnectCallback.onConnectStatus(status);
        }
    }

    /**
     * 异常的执行状态
     *
     * @param status
     */
    private void excuteError(int status) {
        for (IBleClientCallback iBleClientCallback : mIBleClientCallbackList) {
            iBleClientCallback.onError(status);
        }
    }

    /**
     * 设置连接状态的回调
     *
     * @param iBleClientConnectCallback 连接状态的回调
     */
    public void setIBleClientConnectCallback(IBleClientConnectCallback iBleClientConnectCallback) {
        mIBleClientConnectCallbackList.add(iBleClientConnectCallback);
    }

    /**
     * 设置连接后得到各状态的回调
     *
     * @param iBleClientCallback
     */
    public void setIBleGattCallback(IBleClientCallback iBleClientCallback) {
        mIBleClientCallbackList.add(iBleClientCallback);
    }

    /**
     * 设置连接状态的回调
     *
     * @param iBleClientConnectCallback 连接状态的回调
     */
    public void removeIBleClientConnectCallback(IBleClientConnectCallback iBleClientConnectCallback) {
        mIBleClientConnectCallbackList.remove(iBleClientConnectCallback);
    }

    /**
     * 设置连接后得到各状态的回调
     *
     * @param iBleClientCallback
     */
    public void removeIBleGattCallback(IBleClientCallback iBleClientCallback) {
        mIBleClientCallbackList.remove(iBleClientCallback);
    }

    public List<IBleClientCallback> getIBleClientCallbackList() {
        return mIBleClientCallbackList;
    }

}
