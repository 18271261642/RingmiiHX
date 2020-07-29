package hat.bemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;

import hat.bemo.APIupload.Controller;
import hat.bemo.DataBase.Insert;
import hat.bemo.VO.VO_0x30;
import hat.bemo.location.GpsController;
import hat.bemo.measure.database.Update;

public class MarkLocation { // implements BDLocationListener{
	
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
	
	public  MarkLocation(int type){
		LocType = type;
		
		Intent batteryIntent = MyApplication.context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);		
		MyApplication.BatteryInformation.Voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
		MyApplication.BatteryInformation.VoltagePercent = (((float)level / (float)scale) * 100.0f);
			    
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

	/*
	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}
*/
}
