package hat.bemo.BlueTooth.blegatt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import com.guider.healthring.R;


public class BLEMainActivity extends Activity {
    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
	public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
	public final static String EXTRA_DATA = "EXTRA_DATA";
	public static Context mContext;
	public static TextView textView1;
	public static String imei;
//	VERSION API21 jim
//	private BluetoothLeScanner mBluetoothLeScanner;
	
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mContext = this;		
	
		textView1 = (TextView)findViewById(R.id.maintitle);
 
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		
		Intent mIntent = new Intent(BLEMainActivity.this, BLEScanService.class);
		startService(mIntent);
		
		new Thread(new Runnable() {

			@SuppressLint("HandlerLeak")
			@Override
			public void run() {
				Looper.prepare();	
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {
						mHandler = new Handler() {
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								textView1.setText(s1);
							}
						};
				    }
				});
				Looper.loop();
			}
		}).start();
	}
//    private ScanCallback mScanCallback = new ScanCallback() {
//
//		 @Override
//		public void onScanResult(int callbackType, ScanResult result) {
//			super.onScanResult(callbackType, result);
//			BluetoothDevice mBluetoothDevice = result.getDevice();
//			mBluetoothDevice.getName();
//		}
//    };
 
    static String s1;
    static  Handler mHandler;
	@SuppressWarnings("deprecation")
	@SuppressLint("Wakelock")
	public static void ScreenOn(String ss1) {
			PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);  
			final PowerManager.WakeLock mPowerWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK  
												                        | PowerManager.FULL_WAKE_LOCK  
												                        | PowerManager.ACQUIRE_CAUSES_WAKEUP  
												                        | PowerManager.ON_AFTER_RELEASE, "TAG");  
																		mPowerWakeLock.acquire();	
			s1 = ss1;	
			mHandler.sendEmptyMessage(0);
	}
 
	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
			Log.e("", e.toString());
		}
		return version;
	}
}