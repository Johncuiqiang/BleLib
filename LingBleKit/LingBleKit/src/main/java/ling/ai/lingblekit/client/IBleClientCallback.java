package ling.ai.lingblekit.client;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;


/**
 * Created by cuiqiang on 18-1-5.
 *
 * @author cuiqiang
 */

public interface IBleClientCallback {

    void onConnectionStateChange(BluetoothGatt gatt, int status) ;

    void onServicesDiscovered(BluetoothGatt gatt);

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    void onReceiveMessage(int bleCommandType, String message);

    void onError(int status);
}
