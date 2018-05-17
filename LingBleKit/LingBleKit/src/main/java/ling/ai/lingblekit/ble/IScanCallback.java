package ling.ai.lingblekit.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

/**
 * Created by cuiqiang on 18-1-11.
 *
 * @author cuiqiang
 */

public interface IScanCallback {

    void onBleScan(int callbackType, ScanResult result);

    void onClassicBleScan(int scanType, BluetoothDevice device, int rssi);

    void onError(int errorCode);

}
