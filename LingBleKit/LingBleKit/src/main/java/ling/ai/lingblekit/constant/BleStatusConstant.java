package ling.ai.lingblekit.constant;

/**
 * Created by cuiqiang on 18-1-5.
 *
 * @author cuiqiang
 */

public class BleStatusConstant {


    public static final String UUID_SAMPLE_NAME_SERVICE = "000fefe-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SAMPLE_NAME_CHARACTERISTIC = "0000ffee-0000-1000-8000-00805f9b34fb";//通信的uuid

    public static final String UUID_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static final String UUID_WRITE_CHARACTERISTI = "00002a07-0000-1000-8000-00805f9b34fb";

    public static final int STATE_DISCONNECTED = 0;//未连接
    public static final int STATE_CONNECTING = 1;//连接中
    public static final int STATE_CONNECTED = 2;//连接成功
    public static final int STATE_BOND_BONDING = 3;//正在配对
    public static final int STATE_BOND_BONDED = 4;//完成配对
    public static final int STATE_BOND_NONE = 5;//取消配对

    public static final int STATE_FAILED = -1;//已结束但未收全
    public static final int STATE_SUCCESS = 0;//已成功结束
    public static final int STATE_ING = 1;// 未结束

}
