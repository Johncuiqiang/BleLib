package ling.ai.lingblekit.client;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import java.util.List;



/**
 * Created by cuiqiang on 18-1-11.
 *
 * @author cuiqiang
 */

public class ClientGattManager {

    private Context mContext;
    private ClientGattTool mClientGattTool;
    private String mBluetoothDeviceAddress;//上一次蓝牙连接的地址

    public ClientGattManager(Context context) {
        mContext = context;
        mClientGattTool = new ClientGattTool(mContext);
    }

    /**
     * 连接蓝牙
     * @param address 蓝牙地址
     * @return true连接成功 false连接失败
     */
    public boolean connect(final String address) {
        mBluetoothDeviceAddress = address;
        return mClientGattTool.connect(address);
    }

    public boolean reConnect(){
        return mClientGattTool.connect(mBluetoothDeviceAddress);
    }

    /**
     * 搜索服务
     */
    public void discoverServices() {
        mClientGattTool.discoverServices();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        mClientGattTool.disconnect();
    }

    /**
     * 释放的方法
     */
    public void release() {
        mClientGattTool.release();
    }

    /**
     * 读取属性
     *
     * @param characteristic 特征
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        mClientGattTool.readCharacteristic(characteristic);
    }


    /**
     * 发通知
     *
     * @param characteristic 特征
     * @param enabled        true 通知可用 false通知不可用
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                                 boolean enabled) {
        return mClientGattTool.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * 得到所有服务
     *
     * @return 所有服务
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        return mClientGattTool.getSupportedGattServices();
    }

    /**
     * 写入属性
     *
     * @param characteristic 特征
     * @param bytes          写入数据
     * @return true写入成功 false写入失败
     */
    public boolean sendMessage(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        return mClientGattTool.writeCharacteristic(characteristic, bytes);
    }

    /**
     * 设置一次最大传输多少字节
     *
     * @param size 23 - 512
     */
    public void setMtu(int size) {
        if (size <= 23) {
            size = 23;
        } else if (size > 512) {
            size = 512;
        }
        mClientGattTool.setMtu(size);

    }

    /**
     * 判断特征可读
     */
    public boolean ifCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return mClientGattTool.ifCharacteristicReadable(characteristic);
    }

    /**
     * 判断特征可写
     */
    public boolean ifCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return mClientGattTool.ifCharacteristicWritable(characteristic);
    }

    /**
     * 判断特征是否具备通知属性
     */
    public boolean ifCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return mClientGattTool.ifCharacteristicNotifiable(characteristic);
    }

    /**
     * 设置连接状态的回调
     */
    public void setIBleClientConnectCallback(IBleClientConnectCallback iBleClientConnectCallback) {
        mClientGattTool.setIBleClientConnectCallback(iBleClientConnectCallback);
    }

    /**
     * 设置连接后得到各状态的回调
     */
    public void setIBleGattCallback(IBleClientCallback iBleClientCallback) {
        mClientGattTool.setIBleGattCallback(iBleClientCallback);
    }
    /**
     * 设置连接状态的回调
     *
     */
    public void removeIBleClientConnectCallback(IBleClientConnectCallback iBleClientConnectCallback){
        mClientGattTool.removeIBleClientConnectCallback(iBleClientConnectCallback);
    }
    /**
     * 设置连接后得到各状态的回调
     */
    public void removeIBleGattCallback(IBleClientCallback iBleClientCallback){
        mClientGattTool.removeIBleGattCallback(iBleClientCallback);

    }

    public List<IBleClientCallback> getIBleGlientCallbackList(){
        return mClientGattTool.getIBleClientCallbackList();
    }

}
