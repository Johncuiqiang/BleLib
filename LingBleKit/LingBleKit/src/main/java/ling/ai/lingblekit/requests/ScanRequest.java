package ling.ai.lingblekit.requests;

/**
 * Created by cuiqiang on 18-1-9.
 *
 * @author cuiqiang
 */

public class ScanRequest {

    /**时间单位全部精确到毫秒*/
    private int mScanTimes = 1;//扫描次数
    private long mScanBleTime = 20 * 1000;//扫描时间
    private long mScanClassicBleTime =  20 * 1000;//扫描时间
    private long mIntervalTime = 0;//每次扫描的间隔时间

    /**
     * 扫描ble设备
     * @param scanTime 扫描时间
     */
    public ScanRequest searchBle(long scanTime){
        mScanBleTime = scanTime;
        return this;
    }

    /**
     * 扫描传统蓝牙设备
     * @param scanTime 扫描时间
     */
    public ScanRequest searchClassicBle(long scanTime){
        mScanClassicBleTime = scanTime;
        return this;
    }

    /**
     * 设置间隔时间
     * @param intervalTime 间隔时间
     */
    public ScanRequest setIntervalTime(long intervalTime){
        mIntervalTime = intervalTime;
        return this;
    }

    /**
     * 设置扫描次数
     * @param times 扫描时间
     */
    public ScanRequest setScanTimes(int times){
        mScanTimes = times;
        return this;
    }

    /**
     * 得到扫描次数
     * @return 次数
     */
    public int getScanTimes() {
        return mScanTimes;
    }

    /**
     * 得到扫描ble设备的时间
     * @return 扫描时间
     */
    public long getScanBleTime() {
        return mScanBleTime;
    }

    /**
     * 得到扫描传统蓝牙设备的时间
     * @return 扫描时间
     */
    public long getScanClassicBleTime() {
        return mScanClassicBleTime;
    }

    /**
     * 得到每次扫描的间隔时间
     * @return 间隔时间
     */
    public long getIntervalTime() {
        return mIntervalTime;
    }
}
