package hat.bemo.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2017/10/23.
 */

public class WifiAdmin {


    // 定義WifiManager對象
    private WifiManager mWifiManager;
    // 定義WifiInfo對象
    private WifiInfo mWifiInfo;
    // 掃描出的網络連接列表
    private ArrayList<String> mWifiList;
    private ArrayList<ScanResult> mWifiList2;
    // 網络連接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定義一個WifiLock
    WifiManager.WifiLock mWifiLock;
    private String TAG="WifiAdmin";

    // 構造器
    public WifiAdmin(Context context) {
        // 取得WifiManager對象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo對象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    // 打開WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
//            Log.i(TAG, "打開WIFI:"+mWifiManager);
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 關閉WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
//            Log.i(TAG, "關閉WIFI:"+mWifiManager);
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 檢查當前WIFI狀態
    public int checkState() {
        mWifiManager.getWifiState();
//        Log.i(TAG, "檢查當前WIFI狀態 :"+mWifiManager);
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
//        Log.i(TAG, "锁定WifiLock :"+mWifiLock);
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判斷時候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
//            Log.i(TAG, "锁定WifiLock :"+mWifiLock);
        }
    }

    // 創建一個WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
//        Log.i(TAG, "創建一個WifiLock:"+mWifiLock);
    }

    // 得到配置好的網络
    public List<WifiConfiguration> getConfiguration() {
//        Log.i(TAG, "得到配置好的網络:"+mWifiConfiguration);
        return mWifiConfiguration;
    }

    // 指定配置好的網络進行連接
    public void connectConfiguration(int index) {
        // 索引大於配置好的網络索引返回
        if (index > mWifiConfiguration.size()) {
//            Log.i(TAG, "指定配置好的網络進行連接:"+index);
            return;
        }
        // 連接配置好的指定ID的網络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,true);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ArrayList<ScanResult> startScan2() {
        try{
            mWifiManager.startScan();
//          Log.e(TAG, "得到WIFI網路:"+mWifiManager);
            // 得到掃描結果
            mWifiList2 = new  ArrayList<ScanResult>();
            mWifiList2.clear();
            mWifiList2 = ((ArrayList)mWifiManager.getScanResults());
//          Log.e(TAG, "得到掃描結果:"+mWifiList);
            // 得到配置好的網络連接
            mWifiConfiguration = mWifiManager.getConfiguredNetworks();
//          Log.e(TAG, "得到配置好的網络連接:"+mWifiConfiguration);
            return mWifiList2;
        }catch(Exception e){
            e.printStackTrace();
        }
        return mWifiList2;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ArrayList<ScanResult> clear(){
        mWifiManager.startScan();
        mWifiList2 = new  ArrayList<ScanResult>();
        mWifiList2 = ((ArrayList)mWifiManager.getScanResults());
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        mWifiList2.clear();
        return mWifiList2;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void startScan() {
        try{
            mWifiManager.startScan();
//	        Log.e(TAG, "得到WIFI網路:"+mWifiManager);
//	         得到掃描結果
            mWifiList = new  ArrayList<String>();
            mWifiList.clear();
            mWifiList = ((ArrayList)mWifiManager.getScanResults());
//	        Log.e(TAG, "得到掃描結果:"+mWifiList);
//	         得到配置好的網络連接
            mWifiConfiguration = mWifiManager.getConfiguredNetworks();
//	        Log.e(TAG, "得到配置好的網络連接:"+mWifiConfiguration);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 得到網络列表
    public ArrayList<String> getWifiList() {
//        Log.i(TAG, "得到網络列表:"+mWifiList);
        return mWifiList;
    }

    // 查看掃描結果
    @SuppressLint("UseValueOf")
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
//            Log.i(TAG, "查看掃描結果:"+mWifiList);
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // 將ScanResult信息轉換成一個字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        mWifiInfo.getMacAddress();
//        Log.i(TAG, "得到MAC地址 :"+mWifiInfo);
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入點的SSID
    public String getSSID() {
        mWifiInfo.getSSID();
//        Log.i(TAG, "得到接入點的SSID :"+mWifiInfo);
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到接入點的BSSID
    public String getBSSID() {
        mWifiInfo.getBSSID();
//        Log.i(TAG, "得到接入點的BSSID :"+mWifiInfo);
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public int getIPAddress() {
        mWifiInfo.getIpAddress();
//        Log.i(TAG, "得到IP地址 :"+mWifiInfo);
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到連接的ID
    public int getNetworkId() {
        mWifiInfo.getNetworkId();
//        Log.i(TAG, "得到連接的ID:"+mWifiInfo);
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        mWifiInfo.toString();
//        Log.i(TAG, "得到WifiInfo的所有信息包:"+mWifiInfo);
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一個網络並連接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }

    // 斷開指定ID的網络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    //然後是一個實際應用方法，只驗證過沒有密碼的情況：

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
//        Log.i("SSID",SSID);
//        Log.i("PASSWORD",Password);
        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
//            Log.i("Type","Type1");
//               config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            System.out.println("更改");
//               config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
//        	  Log.i(TAG, "Password2:"+Password);
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
//            Log.i("Type","Type3");
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }else{
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            System.out.println("更改");
        }
        int netId = mWifiManager.addNetwork(config);
        mWifiManager.enableNetwork(netId, true);
        return config;
    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

}
