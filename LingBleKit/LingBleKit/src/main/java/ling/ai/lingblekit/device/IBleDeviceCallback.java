package ling.ai.lingblekit.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by cuiqiang on 18-1-8.
 *
 * @author cuiqiang
 */

public interface IBleDeviceCallback {

    void onStartAdvertiseSuccess();

    void onStartAdvertiseFailure(int errorCode);

    void onCharacteristicReadRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] bytes);

    void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] bytes);

    void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, byte[] bytes);

    void onNetworkMessage(String ssid, String password, String userId);

    void onConnectionStateChange(BluetoothDevice device, int newState);

}
