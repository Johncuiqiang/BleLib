package ling.ai.lingblekit.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by cuiqiang on 18-1-4.
 *
 * @author cuiqiang
 */

public class BleGattHelper {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配者
    private BluetoothManager mBluetoothManager;//蓝牙管理类

    public BleGattHelper(Context context) {
        mContext = context;
    }

    /**
     * 是否支持低功耗模块
     *
     * @return true支持 false不支持
     */
    public boolean initBle() {
        //检测是否支持BLE
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        if (null == mBluetoothManager) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        return true;
    }


    /**
     * 打开蓝牙
     */
    public boolean openBle() {
        if (null == mBluetoothAdapter && !initBle()) {
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            return mBluetoothAdapter.enable();
        }
        return true;
    }

    /**
     * 关闭蓝牙并释放资源
     */
    public void closeBle() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothManager getBluetoothManager() {
        return mBluetoothManager;
    }


}
