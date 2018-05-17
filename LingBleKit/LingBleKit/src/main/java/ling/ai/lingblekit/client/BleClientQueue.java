package ling.ai.lingblekit.client;

import java.util.LinkedHashMap;

/**
 * Created by cuiqiang on 18-1-11.
 *
 * @author cuiqiang
 */

public class BleClientQueue {

    private LinkedHashMap<String,ClientGattManager> mBleClientMap = new LinkedHashMap<>();

    public void addClienttManager(String deviceName, ClientGattManager clientGattManager){
        mBleClientMap.put(deviceName,clientGattManager);
    }

    public void removeClientManager(String deviceName){
        mBleClientMap.remove(deviceName);
    }

    public ClientGattManager getClientManager(String deviceName){
       return mBleClientMap.get(deviceName);
    }

    public void clear(){
        mBleClientMap.clear();
    }
}
