package ling.ai.lingblekit;

import android.content.Context;

import ling.ai.lingblekit.ble.BleGattHelper;
import ling.ai.lingblekit.ble.BleScanManager;
import ling.ai.lingblekit.client.ClientGattManager;
import ling.ai.lingblekit.device.ServerGattManager;

/**
 * Created by cuiqiang on 18-1-2.
 *
 * @author cuiqiang
 */

public class BleManager {

    private static BleManager INSTANCE;

    private Context mContext;
    private BleGattHelper mBleGattHelper;//蓝牙管理辅助类
    private BleScanManager mBleScanManager;//蓝牙扫描管理者
    private ServerGattManager mServerGattManager;//蓝牙服务端管理者
    private ClientGattManager mClientGattManager;//蓝牙客户端管理者

    public static BleManager getInstance() {
        if (null == INSTANCE) {
            synchronized (BleManager.class) {
                if (null == INSTANCE) {
                    INSTANCE = new BleManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化方法
     *
     * @return false不支持ble true初始化成功
     */
    public BleManager init(Context context) {
        //此处的context暂不能转为application的context,
        //动态权限以及跳转蓝牙设置界面必须是activity,否则会报错
        mContext = context;
        mBleGattHelper = new BleGattHelper(mContext);
        mBleGattHelper.initBle();
        return this;
    }

    public boolean isSupportBle(){
        return mBleGattHelper != null;
    }

    /**
     * 初始化蓝牙扫描者
     */
    public BleScanManager initScanBle() {
        mBleScanManager = new BleScanManager(mContext);
        if (mBleGattHelper != null) {
            mBleScanManager.init();
        }
        return mBleScanManager;
    }

    /**
     * 打开蓝牙
     */
    public boolean openBle() {
        if (mBleGattHelper != null) {
            return mBleGattHelper.openBle();
        }
        return false;
    }

    /**
     * 判断服务端端有无蓝牙连接
     *
     * @return true有 false无
     */
    public boolean isServerConnected() {
        if (null != mServerGattManager) {
            return mServerGattManager.isConnected();
        }
        return false;
    }

    /**
     * 初始化服务端
     */
    public ServerGattManager initBleServer() {
        mServerGattManager = new ServerGattManager(mContext);
        return mServerGattManager;
    }

    /**
     * 释放蓝牙服务端
     */
    public void releaseBleServer() {
        if (null != mServerGattManager) {
            mServerGattManager.release();
            mServerGattManager = null;
        }
    }

    /**
     * 初始化蓝牙客户端
     */
    public ClientGattManager initBleClient() {
        mClientGattManager = new ClientGattManager(mContext);
        return mClientGattManager;
    }

    /**
     * 释放蓝牙客户端
     */
    public void releaseBleClient() {
        if (mClientGattManager != null) {
            mClientGattManager.release();
        }
    }

    /**
     * 广播ble
     */
    public void closeBle() {
        release();
        mBleGattHelper.closeBle();
    }

    /**
     * 释放蓝牙扫描者
     */
    private void releaseBleScan() {
        if (null != mBleScanManager) {
            mBleScanManager.release();
        }
    }

    /**
     * 获得服务端蓝牙管理者
     *
     * @return 服务端蓝牙管理者
     */
    public ServerGattManager getServerGattManager() {
        return mServerGattManager;
    }

    /**
     * 获得客户端蓝牙管理者
     *
     * @return 客户端蓝牙管理者
     */
    public ClientGattManager getClientGattManager() {
        return mClientGattManager;
    }

    /**
     * 得到蓝牙扫描者
     *
     * @return 蓝牙扫描者
     */
    public BleScanManager getBleScanManager() {
        return mBleScanManager;
    }

    /**
     * 释放蓝牙相关数据
     */
    public void release() {
        releaseBleServer();
        releaseBleClient();
        releaseBleScan();
    }

    public BleGattHelper getBleGattHelper() {
        return mBleGattHelper;
    }
}
