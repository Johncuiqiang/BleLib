package ling.ai.lingblekit.requests;

/**
 * Created by cuiqiang on 18-1-9.
 *
 * @author cuiqiang
 */

public class MessageRequest {

    private int mRetryTimes = 0;//重试次数
    /**
     * 每次发送数据的间隔时间,只有确认上次发送数据失败才会再次发送
     * socket数据发送状态有可能不准,建议socket通信双方协议确认协议
     */
    private long mIntervalTime = 0;
    private long mTimeout = 0;//第一条数据的超时时间

    /**
     * 设置重试次数
     * @param mRetryTimes 重试次数
     */
    public MessageRequest setRetryTimes(int mRetryTimes) {
        this.mRetryTimes = mRetryTimes;
        return this;
    }

    /**
     * 设置间隔时间
     * @param mIntervalTime 间隔时间
     */
    public MessageRequest setIntervalTime(long mIntervalTime) {
        this.mIntervalTime = mIntervalTime;
        return this;
    }

    /**
     * 设置超时时间
     * @param mTimeout 超时时间
     */
    public MessageRequest setTimeout(long mTimeout) {
        this.mTimeout = mTimeout;
        return this;
    }

    /**
     * 得到超时时间
     * @return 超时时间
     */
    public long getTimeout() {
        return mTimeout;
    }

    /**
     * 得到间隔时间
     * @return 间隔时间
     */
    public long getIntervalTime() {
        return mIntervalTime;
    }

    /**
     * 得到重试次数
     * @return 重试次数
     */
    public int getRetryTimes() {
        return mRetryTimes;
    }
}
