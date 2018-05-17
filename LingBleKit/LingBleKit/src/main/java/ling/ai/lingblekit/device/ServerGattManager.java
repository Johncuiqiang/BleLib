package ling.ai.lingblekit.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import java.util.List;


/**
 * Created by cuiqiang on 18-1-4.
 *
 * @author cuiqiang
 */

public class ServerGattManager {

    private static final String TAG = "ble";

    private Context mContext;
    private BleGattServerTool mBleGattServerTool;//蓝牙外设模块
    private boolean isConnected = false;//蓝牙是否连接
    private IBleDeviceCallback mIBleDeviceNetCallback;
    private IBleDeviceCallback mIBleDeviceCallback = new IBleDeviceCallback() {
        @Override
        public void onStartAdvertiseSuccess() {

        }

        @Override
        public void onStartAdvertiseFailure(int errorCode) {

        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] bytes) {

        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] bytes) {

        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, byte[] bytes) {

        }

        @Override
        public void onNetworkMessage(String ssid, String passwork, String userId) {

        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int newState) {
            setConnectState(device, newState);
        }
    };

    public ServerGattManager(Context context) {
        this.mContext = context;
        initBle();
    }

    /**
     * 设置回调
     * 避免重复
     */
    public void setIBleDeviceCallback(IBleDeviceCallback iBleDeviceCallback) {
        removeIBleDeviceCallback(mIBleDeviceNetCallback);
        mIBleDeviceNetCallback = iBleDeviceCallback;
        mBleGattServerTool.setIBleDeviceCallbackList(mIBleDeviceNetCallback);
    }

    /**
     * 移除回调
     */
    public void removeIBleDeviceCallback(IBleDeviceCallback iBleDeviceCallback) {
        mBleGattServerTool.removeIBleDeviceCallbackList(iBleDeviceCallback);
    }

    /**
     * 增加回调
     */
    public void addIBleDeviceCallback(IBleDeviceCallback iBleDeviceCallback) {
        mBleGattServerTool.setIBleDeviceCallbackList(mIBleDeviceNetCallback);
    }

    private void initBle() {
        mBleGattServerTool = new BleGattServerTool(mContext);
        mBleGattServerTool.setIBleDeviceCallbackList(mIBleDeviceCallback);
    }


    /**
     * 设置连接状态
     */
    private void setConnectState(BluetoothDevice device, int newState) {
        switch (newState) {
            case BluetoothProfile.STATE_DISCONNECTING:
                Log.i(TAG ,device.getName() + " 正在断开");
                isConnected = false;
                break;
            case BluetoothProfile.STATE_CONNECTING:
                Log.i(TAG , device.getName() + " 正在连接");
                isConnected = false;
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                Log.i(TAG , device.getName() + " 已断开");
                isConnected = false;
                break;
            case BluetoothProfile.STATE_CONNECTED:
                Log.i(TAG ,  device.getName() + " 已连接");
                isConnected = true;
                break;
            default:
                break;
        }
    }

    /**
     * 释放法消息
     */
    public void release() {
        mBleGattServerTool.release();
    }

    public boolean sendMessage(byte[] data) {
       return mBleGattServerTool.sendMessage(data);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<IBleDeviceCallback> getIBleDeviceCallbackList() {
        return mBleGattServerTool.getIBleDeviceCallbackList();
    }
}
