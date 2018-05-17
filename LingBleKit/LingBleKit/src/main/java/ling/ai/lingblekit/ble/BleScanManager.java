package ling.ai.lingblekit.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ling.ai.lingblekit.BleManager;
import ling.ai.lingblekit.requests.ScanRequest;


/**
 * Created by cuiqiang on 18-1-11.
 *
 * @author cuiqiang
 */

public class BleScanManager {

    // TODO: callbackType需要自己定义
    private WeakReference<Context> mContextReference;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private List<IScanCallback> mScanCallbackList = new LinkedList<>();
    private ScanRequest mScanRequest;//默认的扫描参数
    private boolean isScanning = false;//是否在扫描
    private Timer mTimer;

    /**
     * ble扫描的回调
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            for (IScanCallback iScanCallback : mScanCallbackList) {
                iScanCallback.onBleScan(callbackType, result);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback mScanAdapterCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            for (IScanCallback iScanCallback : mScanCallbackList) {
                iScanCallback.onClassicBleScan(0, device, rssi);
            }
        }
    };


    public BleScanManager(Context context) {
        mContextReference = new WeakReference<>(context);
    }

    public void init() {
        BleGattHelper bleGattHelper = BleManager.getInstance().getBleGattHelper();
        mBluetoothAdapter = bleGattHelper.getBluetoothAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        initBLeScan();
        mScanRequest = new ScanRequest();
    }

    /**
     * 设置扫描参数
     *
     * @param scanRequest 扫描参数
     */
    public void setScanRequest(ScanRequest scanRequest) {
        this.mScanRequest = scanRequest;
    }

    /**
     * 设置扫描的回调
     */
    public void setScanCallbackList(IScanCallback scanCallback) {
        this.mScanCallbackList.add(scanCallback);
    }

    /**
     * 移除扫描的回调
     */
    public void removeScanCallbackList(IScanCallback scanCallback) {
        this.mScanCallbackList.remove(scanCallback);
    }

    /**
     * 开始扫描
     */
    public void startScan() {
        if (!mBluetoothAdapter.isEnabled()) {
            return;
        }
        if (mBluetoothLeScanner == null) {
            init();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isScanning) {
                mBluetoothLeScanner.startScan(mScanCallback);
                isScanning = true;
                excuteScan();
            }
        } else {
            if (!isScanning) {
                mBluetoothAdapter.startLeScan(mScanAdapterCallback);
                isScanning = true;
                excuteScan();
            }
        }
    }


    /**
     * 初始化BLE设备扫描者
     */
    private void initBLeScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null != mBluetoothAdapter) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
        }
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (!mBluetoothAdapter.isEnabled()) {
            return;
        }
        if (mBluetoothLeScanner != null) {
            cancelTimer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mScanAdapterCallback);
            }
            isScanning = false;
        }
    }

    /**
     * 是否正在扫描
     *
     * @return true是 false不是
     */
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * 释放资源不关闭蓝牙
     */
    public void release() {
        isScanning = false;
        mScanCallbackList.clear();
        stopScan();
    }

    /**
     * 设置扫描时间限制
     */
    private void excuteScan() {
        long scanBleTime = mScanRequest.getScanBleTime();
        cancelTimer();
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stopScan();
                }
            }, scanBleTime);
        }
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

}
