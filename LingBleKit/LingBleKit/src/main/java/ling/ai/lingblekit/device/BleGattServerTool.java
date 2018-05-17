package ling.ai.lingblekit.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LogPrinter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import ling.ai.lingblekit.ble.BleGattHelper;
import static ling.ai.lingblekit.constant.BleStatusConstant.UUID_SERVICE;
import static ling.ai.lingblekit.constant.BleStatusConstant.UUID_WRITE_CHARACTERISTI;


/**
 * Created by cuiqiang on 18-1-9.
 *
 * @author cuiqiang
 */

public class BleGattServerTool {

    private static final String TAG = "ble";

    private Context mContext;
    private BluetoothDevice mDevice;
    private BleGattHelper mBleGattHelper;
    private List<IBleDeviceCallback> mIBleDeviceCallbackList = new LinkedList<>();
    private BluetoothGattServer mGattServer;//gatt的服务
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;//广告模式对象
    private BluetoothGattCharacteristic mBleGattCharacteristicWrite;//可通信的服务


    //广告模式是否开启成功
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG, " 开启服务成功");
            for (IBleDeviceCallback iBleDeviceCallback : mIBleDeviceCallbackList) {
                iBleDeviceCallback.onStartAdvertiseSuccess();
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.i(TAG,  " 开启服务失败" + errorCode);
            for (IBleDeviceCallback iBleDeviceCallback : mIBleDeviceCallbackList) {
                iBleDeviceCallback.onStartAdvertiseFailure(errorCode);
            }
        }
    };

    public BleGattServerTool(Context context) {
        this.mContext = context;
        initBle();
    }

    public void setIBleDeviceCallbackList(IBleDeviceCallback iBleDeviceCallback) {
        mIBleDeviceCallbackList.add(iBleDeviceCallback);
    }

    public void removeIBleDeviceCallbackList(IBleDeviceCallback iBleDeviceCallback) {
        mIBleDeviceCallbackList.remove(iBleDeviceCallback);
    }

    private void initBle() {
        mBleGattHelper = new BleGattHelper(mContext);
        mBleGattHelper.initBle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BluetoothAdapter bluetoothAdapter = mBleGattHelper.getBluetoothAdapter();
            if (bluetoothAdapter != null) {
                mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            }
        }
        if (mBluetoothLeAdvertiser == null) {
            Log.i(TAG,  " 不支持ble功能");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startGattServer();
        }
    }


    /**
     * 设置广播数据
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseSettings createAdvSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        builder.setConnectable(true);
        builder.setTimeout(0);
        //ADVERTISE_MODE_LOW_POWER 在低功耗模式下执行蓝牙LE广告。这是默认和首选的广告模式，因为它消耗最少的电力。
        //ADVERTISE_MODE_BALANCED 在平衡电源模式下执行蓝牙LE广告。这是广告频率和功耗之间的平衡。
        //ADVERTISE_MODE_LOW_LATENCY 在低延迟，高功率模式下执行蓝牙LE广告。这是最高的功耗，不应该用于连续的背景广告。
        //目前选用最高的功耗,广播效果较好
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        return builder.build();
    }

    /**
     * 设置响应数据
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseData createAdvData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addServiceUuid(ParcelUuid.fromString(UUID_SERVICE));
//        byte mLeManufacturerData[] = {(byte) 0x4C, (byte) 0x00, (byte) 0x02, (byte) 0x15, (byte) 0x15, (byte) 0x15, (byte) 0x15};
//        builder.addManufacturerData(0x3103 + 1, mLeManufacturerData);
        builder.setIncludeTxPowerLevel(false);
        builder.setIncludeDeviceName(true);
        return builder.build();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startGattServer() {
        Log.i(TAG,  " 开启服务ing");
        mGattServer = mBleGattHelper.getBluetoothManager().openGattServer(mContext, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                switch (newState) {
                    case BluetoothProfile.STATE_DISCONNECTED:
                        mDevice = null;
                        Log.i(TAG,  device.getName() + " 断开");
                        break;
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.i(TAG,  device.getName() + " 连接");
                        mDevice = device;
                        break;
                    default:
                        break;
                }
                for (IBleDeviceCallback iBleDeviceCallback : mIBleDeviceCallbackList) {
                    iBleDeviceCallback.onConnectionStateChange(device, newState);
                }
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                Log.i(TAG,  " onServiceAdded");
            }

            /**A remote client has requested to read a local characteristic.*/
            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                Log.i(TAG, " onCharacteristicReadRequest ");
                mGattServer.sendResponse(mDevice, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            }

            /**
             * 接收消息的方法 - 接收具体的字节
             */
            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                //在确认模式下,收到消息需要设备端回执否则通信只能进行一次
                Log.i(TAG,  " onCharacteristicWriteRequest");
                mGattServer.sendResponse(mDevice, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
                for (IBleDeviceCallback iBleDeviceCallback : mIBleDeviceCallbackList) {
                    iBleDeviceCallback.onCharacteristicWriteRequest(device, requestId, characteristic, value);
                }
            }

            /**
             *  特征被读取。当回复响应成功后，客户端会读取然后触发本方法
             *
             */
            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                Log.i(TAG, " DescriptorRead");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            }

            /**
             * 2.描述被写入时
             */
            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                Log.i(TAG,  " DescriptorWrite");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
                for (IBleDeviceCallback iBleDeviceCallback : mIBleDeviceCallbackList) {
                    iBleDeviceCallback.onDescriptorWriteRequest(device, requestId, descriptor, value);
                }
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                Log.i(TAG,  " onExecuteWrite");
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
                Log.i(TAG, " onNotificationSent");
            }
        });

        //初始化GATT服务
        BluetoothGattService nameService = new BluetoothGattService(UUID.fromString(UUID_SERVICE),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //增加读写特征
        mBleGattCharacteristicWrite = new BluetoothGattCharacteristic(
                UUID.fromString(UUID_WRITE_CHARACTERISTI),
                BluetoothGattCharacteristic.PROPERTY_READ
                        | BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ
                        | BluetoothGattCharacteristic.PERMISSION_WRITE
        );
        mBleGattCharacteristicWrite.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        //添加描述
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(
                UUID.fromString(UUID_WRITE_CHARACTERISTI)
                , BluetoothGattCharacteristic.PERMISSION_WRITE);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBleGattCharacteristicWrite.addDescriptor(descriptor);
        nameService.addCharacteristic(mBleGattCharacteristicWrite);
        mGattServer.addService(nameService);
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(), createAdvData(), mAdvertiseCallback);
    }

    /**
     * 发送数据给连接的设备
     *
     * @param data 数据
     */
    public boolean sendMessage(byte[] data) {
        if (mDevice == null || mBleGattCharacteristicWrite == null || data == null) {
            return false;
        }
        mBleGattCharacteristicWrite.setValue(data);
        return mGattServer.notifyCharacteristicChanged(mDevice, mBleGattCharacteristicWrite, false);
    }

    /**
     * 释放法消息
     */
    public void release() {
        if (null != mGattServer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            }
            mIBleDeviceCallbackList.clear();
            mGattServer.clearServices();
            mGattServer.close();
            mGattServer = null;
        }
    }

    public List<IBleDeviceCallback> getIBleDeviceCallbackList() {
        return mIBleDeviceCallbackList;
    }
}
