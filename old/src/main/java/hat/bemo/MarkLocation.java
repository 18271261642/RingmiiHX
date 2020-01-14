package hat.bemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import hat.bemo.APIupload.ChangeDateFormat;
import hat.bemo.APIupload.Controller;
import hat.bemo.DataBase.Insert;
import hat.bemo.VO.VO_0x30;
import hat.bemo.location.GpsController;
import hat.bemo.measure.database.Update;

public class MarkLocation implements BDLocationListener{
	
	 private VO_0x30 vo0x30 = new VO_0x30();
	 private String gps_status = "1";
	    private String createdate; 
	    private String LAT="";
	    private String LONG="";
	    private String address="";
	    private String Accuracy = "0";
	    
	    private String MCC="0";
	    private String MNC="0";
	    private String LAC="0";
	    private String CELL_ID="0";
	    private String RSSI="-79";
		
	    private String WIFI_MAC="0";
	    private String WIFI_Signal_dB="0";
	    private String WIFI_Channel="0";
	    private Insert insert = new Insert();
	    private String Emg_Date;
//		private VO_0x01_Insert insert_x01 = new VO_0x01_Insert();
		private String[] ITEMNO = {"1"};
		private Update up;
		private String PWR_OFF_TYPE= "1"; 
		
	    //Mark 1-同步, 2-關機, 3-SOS, 4-主動(暫時用SOS)。
	    private int LocType;
	    
	    private Handler MarkHandler1, MarkHandler2, MarkHandler3;
	    
	    public static double pi = 3.1415926535897932384626;  
	    public static double a = 6378245.0;  
	    public static double ee = 0.00669342162296594323;  
	
	public LocationClient mLocationClient = null;
	private MyLocationListener myListener = new MyLocationListener();
	
	//BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
	//原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    private WifiManager mWifiManager;
	
	public  MarkLocation(int type){
		LocType = type;
		
		Intent batteryIntent = MyApplication.context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);		
		MyApplication.BatteryInformation.Voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
		MyApplication.BatteryInformation.VoltagePercent = (((float)level / (float)scale) * 100.0f);
		
			 mLocationClient = new LocationClient(MyApplication.context);     
			    //声明LocationClient类
			    mLocationClient.registerLocationListener(myListener);    
			    //注册监听函数
//			    mWifiManager.setWifiEnabled(true);
			    initBaiduLocation();
			    mLocationClient.start();
			    
			    if(LocType == 1){
//					UploadSyncLocation();
				}
				else if(LocType == 2){
//					UploadPowerOffLocation();
				}
				else if(LocType == 3){
					
					MarkHandler1 = new Handler();
					MarkHandler1.postDelayed(SOSUpload1, 30*1000);
					
					MarkHandler2 = new Handler();
					MarkHandler2.postDelayed(SOSUpload2, 3*60*1000);
					
					MarkHandler3 = new Handler();
					MarkHandler3.postDelayed(CloseBaidu, 4*60*1000);
					
				}
				else{
					//暫時用SOS
//					UploadSMSLocation();
				}
				
	}
	
	 private void initBaiduLocation() {
		 LocationClientOption option = new LocationClientOption();

		 option.setLocationMode(LocationMode.Hight_Accuracy);
		 option.setLocationMode(LocationMode.Battery_Saving);
		 //可选，设置定位模式，默认高精度
		 //LocationMode.Hight_Accuracy：高精度；
		 //LocationMode. Battery_Saving：低功耗；
		 //LocationMode. Device_Sensors：仅使用设备；
		 	
		 option.setCoorType("gcj02");
		 //可选，设置返回经纬度坐标类型，默认gcj02
		 //gcj02：国测局坐标；
		 //bd09ll：百度经纬度坐标；
		 //bd09：百度墨卡托坐标；
		 //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
		  	
		 option.setScanSpan(1000);
		 //可选，设置发起定位请求的间隔，int类型，单位ms
		 //如果设置为0，则代表单次定位，即仅定位一次，默认为0
		 //如果设置非0，需设置1000ms以上才有效
		 	
		 option.setOpenGps(true);
		 //可选，设置是否使用gps，默认false
		 //使用高精度和仅用设备两种定位模式的，参数必须设置为true
		 	
		 option.setLocationNotify(true);
		 //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
		 	
		 option.setIgnoreKillProcess(false);
		 //可选，定位SDK内部是一个service，并放到了独立进程。
		 //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
		 	
		 option.SetIgnoreCacheException(false);
		 //可选，设置是否收集Crash信息，默认收集，即参数为false

		 option.setWifiCacheTimeOut(5*60*1000);
		 //可选，7.2版本新增能力
		 //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
		 	
		 option.setEnableSimulateGps(false);
		 //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
		 	
		 mLocationClient.setLocOption(option);
		 //mLocationClient为第二步初始化过的LocationClient对象
		 //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
		 //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
	 }
	
	 public class MyLocationListener implements BDLocationListener{
		    public void onReceiveLocation(BDLocation location){
		        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
		        //以下只列举部分获取经纬度相关（常用）的结果信息
		        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
					
		        double latitude = location.getLatitude();    //获取纬度信息
		        double longitude = location.getLongitude();    //获取经度信息
		        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
					
		        String coorType = location.getCoorType();
		        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
					
		        int errorCode = location.getLocType();
		        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
		        
		        System.out.println("Mark lat = " + latitude + ", lon = " + 
		        longitude + ", radius = " + radius + ", coorType = " + 
		        coorType + ", errorCode = " + errorCode);
		        
		        if(errorCode == 161 || errorCode == 61){
		        	
		        	Emg_Date = ChangeDateFormat.CreateDate();
		        	createdate = ChangeDateFormat.CreateDate();
		        	
		        	Accuracy = String.valueOf(radius);
		        	
					gps_status = "0";
					
					if(coorType.equals("gcj02")){
						GCJ02ToWGS84(latitude, longitude);
					}
					else{
						LAT  = String.valueOf(latitude);
						LONG = String.valueOf(longitude);
					}
					
					if(LocType != 3){
						
						mLocationClient.stop();
						
					}
					
		        }
		        
		    }
		}
	 	
	 			public final Runnable SOSUpload1 = new Runnable() {
					public void run() {
						
						System.out.println("Mark SOSUpload1");
						MarkHandler1.removeCallbacks(SOSUpload1);
						UploadSOSLocation();
						
					}
					
				};
				
				 public final Runnable SOSUpload2 = new Runnable() {
						public void run() {
							
							System.out.println("Mark SOSUpload2");
							
							MarkHandler2.removeCallbacks(SOSUpload2);
							UploadSOSLocation();
							
						}
						
					};
					
				 public final Runnable CloseBaidu = new Runnable() {
						public void run() {
								
							System.out.println("Mark CloseBaidu");
							MarkHandler3.removeCallbacks(CloseBaidu);
							mLocationClient.stop();
								
						}
							
					};
	 
	 //SOS
	 public void UploadSOSLocation(){
		 	vo0x30.setEMG_DATE(Emg_Date);	
			vo0x30.setGPS_LAT(LAT);
			vo0x30.setGPS_LNG(LONG);
			vo0x30.setGPS_ADDRESS(address);
			vo0x30.setGPS_ACCURACY(Accuracy);
			vo0x30.setGPS_STATUS(gps_status);	
			vo0x30.setMCC(MCC);
			vo0x30.setMNC(MNC);		 
			vo0x30.setLAC(LAC);
			vo0x30.setCELL_ID(CELL_ID);
			vo0x30.setRSSI(RSSI);
			vo0x30.setWIFI_MAC(WIFI_MAC);
			vo0x30.setWIFI_Signal_dB(WIFI_Signal_dB);
			vo0x30.setWIFI_Channel(WIFI_Channel);
			vo0x30.setCREATE_DATE(createdate);
			insert.insert_0x30(MyApplication.context, vo0x30);				
			MyApplication.cro.LoData(MyApplication.context, Controller.type_0x30);
			GpsController.setGpsStatus(MyApplication.context, false);
	 }
	 
	 //同步
//	 public void UploadSyncLocation(){
//		 	insert_x01.setPR_COUNT("1");
//			insert_x01.setPR_DATE(ChangeDateFormat.CreateDate());
//			insert_x01.setVOLTAGE(BatteryInformation.Voltage+"");
//			insert_x01.setVOLTAGE_PERCENT(BatteryInformation.VoltagePercent+"");
//			insert_x01.setLAC(LAC);
//			insert_x01.setMNC(MNC);
//			insert_x01.setRSSI(RSSI);
//			insert_x01.setCELL_ID(CELL_ID);
//			insert_x01.setMCC(MCC);
//			insert_x01.setGSensor_AVG("0");
//			insert_x01.setGSensor_MAX("0");
//			insert_x01.setPPM("0");
//			insert_x01.setGPS_STATUS(gps_status);
//			insert_x01.setGPS_LAT(LAT);
//			insert_x01.setGPS_LNG(LONG);
//			insert_x01.setGPS_ADDRESS(address);
//			insert_x01.setGPS_ACCURACY(Accuracy);
//			insert_x01.setWIFI_MAC(WIFI_MAC);
//			insert_x01.setWIFI_Signal_dB(WIFI_Signal_dB);
//			insert_x01.setWIFI_Channel(WIFI_Channel);
//			insert.insert_0x01(MyApplication.context, insert_x01);
//			MyApplication.cro.LoData(MyApplication.context, Controller.type_0x01);
//			GpsController.setGpsStatus(MyApplication.context, false);
//
//	 }
	 
	 //關機
//	 public void UploadPowerOffLocation(){
//
//		 if(MyApplication.BatteryInformation.VoltagePercent < 10.0){
//				PWR_OFF_TYPE = "2";
//			}
//
//		 up.up_0x2F(MyApplication.context, ITEMNO, ChangeDateFormat.CreateDate(), ChangeDateFormat.CreateDate(),
//				   BatteryInformation.Voltage+"", BatteryInformation.VoltagePercent+"", MCC, MNC, LAC, CELL_ID,
//				   RSSI, ChangeDateFormat.CreateDate(), "0", LAT, LONG, gps_status,
//				   ChangeDateFormat.CreateDate(), PWR_OFF_TYPE, MyApplication.imei);
//
//		MyApplication.cro.LoData(MyApplication.context, Controller.type_0x2F);
//
//	 }
	 
	 //主動(暫時用SOS)
//	 public void UploadSMSLocation(){
//		 
//	 }
	 
	 public void GCJ02ToWGS84(double lat, double lon){
		 
		 double dlat = transformLat(lon - 105.0, lat - 35.0);
	        double dlng = transformLon(lon - 105.0, lat - 35.0);
	        double radlat = lat / 180.0 * pi;
	        double magic = Math.sin(radlat);
	        magic = 1 - ee * magic * magic;
	        double sqrtmagic = Math.sqrt(magic);
	        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * pi);
	        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * pi);
	        double mglat = lat + dlat;
	        double mglng = lon + dlng;
	        double WGSlat = lat*2 - mglat;
	        double WGSlon = lon*2 - mglng;
	        
	        LAT  = String.valueOf(WGSlat);
			LONG = String.valueOf(WGSlon);
			
			System.out.println("Mark WGS84 LAT = " + LAT + ", LONG = " + LONG);
		 
	 }
	 
	 public static double transformLat(double x, double y) {  
	        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y  
	                + 0.2 * Math.sqrt(Math.abs(x));  
	        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;  
	        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;  
	        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;  
	        return ret;  
	    }  
	  
	    public static double transformLon(double x, double y) {  
	        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1  
	                * Math.sqrt(Math.abs(x));  
	        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;  
	        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;  
	        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0  
	                * pi)) * 2.0 / 3.0;  
	        return ret;  
	    }  
	 
	private void locationServiceInitial() {
		
		
	}
	
	private void getLocation(Location location) {	//將定位資訊顯示在畫面中
		
	}
	
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
	
		
		
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}

}
