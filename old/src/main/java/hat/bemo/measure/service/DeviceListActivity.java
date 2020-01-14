package hat.bemo.measure.service;
/**
 * 描述：此類為掃描和連接藍牙設備彈出框
 * 作用：掃描和連接藍牙設備
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import com.guider.healthring.R;
import hat.bemo.measure.set.Command;

public class DeviceListActivity extends Activity {
    // Debugg
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    private BluetoothAdapter mBtAdapter;
    private BluetoothAdapter mBtAdapter_lvh;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
    private Handler mHandler;
    private boolean mScanning;
 // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 20000;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public final static String ACTION_GATT_CONNECTED ="ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED ="ACTION_GATT_DISCONNECTED";
    
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.gd800_measure_device_list);

        mHandler = new Handler();
        
        setResult(Activity.RESULT_CANCELED);       
        
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.gd800_measure_device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.gd800_measure_device_name);

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
       
//        if(UserInfoFragment.getAndroidSDKVersion() > 18) {
        	ScanCallback();
	        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	        mBtAdapter_lvh = bluetoothManager.getAdapter();
//        }
        
//        if(mBtAdapter_lvh == null){
//        	 Log.e(TAG, "藍牙4.0");            
//        	 scanLeDevice(true);
//        }
//        else{
//        	Log.e(TAG, "藍牙2.0");
        	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            this.registerReceiver(mReceiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            this.registerReceiver(mReceiver, filter);
             
        	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
             if (pairedDevices.size() > 0) {
//	             findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
	             for (BluetoothDevice device : pairedDevices) {
	                 mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	             }
	         } else {
	             String noDevices = getResources().getText(R.string.none_paired).toString();
	             mPairedDevicesArrayAdapter.add(noDevices);
	         }
//       }
        Log.e("", getString(R.string.scanning));
        doDiscovery();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
            this.unregisterReceiver(mReceiver);
           
        }
        
        if (mBtAdapter == null){
        	mBtAdapter_lvh.stopLeScan(mLeScanCallback);
        } 
    }
    
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

//        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);
        if (mBtAdapter != null) {
	        if (mBtAdapter.isDiscovering()) {
	            mBtAdapter.cancelDiscovery();
	        }  
	        mBtAdapter.startDiscovery();
        }
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
     // 獲取本地藍牙適配器
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void scanLeDevice(final boolean enable) {
        if (enable) {    	
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() { 
                @Override
                public void run() {
                	Log.e("BT","掃描結束"); 
                	setTitle(R.string.select_device);
                    mScanning = false;
                    mBtAdapter_lvh.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);   
            
            mScanning = true;
            mBtAdapter_lvh.startLeScan(mLeScanCallback);
        } else {
        	Log.e("BT","掃描結束"); 
        	setTitle(R.string.select_device);
            mScanning = false;
            mBtAdapter_lvh.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void ScanCallback(){
	    // Device scan callback.
		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

	        @Override
	        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	if(device != null){
	                		String devicename = "";
	                		devicename = device.getName();
	                		addDeviceName(devicename, device);     
		                	Log.e("mLeScanCallback",device.getName());
		                	mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	                	}
	                }
	            });
	        }
	    };
	}

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            if(mBtAdapter != null) mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            String name = info.substring(0, info.length() - 17);
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(BluetoothBaseActivity.EXTRA_DEVICE_NAME, name);
            intent.putExtra(BluetoothBaseActivity.EXTRA_DEVICE_ADDRESS, address);
            Log.e("mDeviceClickListener",name);
//          Taidoc-Device 00:12:A1:B0:8A:F7
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	try{
                		String devicename = "";
                		devicename = device.getName();
                		addDeviceName(devicename, device);           		
                	}catch(Exception e){
                		e.printStackTrace();
                	}              	                 
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                Log.e("mReceiver","掃描結束");
//                if (mNewDevicesArrayAdapter.getCount() == 0) {
//                    String noDevices = getResources().getText(R.string.none_found).toString();
//                    mNewDevicesArrayAdapter.add(noDevices);
//                }
            }  
        }
    };
    
    private void addDeviceName(String devicename, BluetoothDevice device){
		if(devicename != null | !(devicename.equals(""))){
			Log.e("BT",device.getName());
			if(devicename.equals(Command.DEVICENAME.TaidocDevice)){
        		mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
        	}
			else if(devicename.equals(Command.DEVICENAME.Taidoctd3128)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
			else if(devicename.equals(Command.DEVICENAME.TaidocTD4279)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress()); 
			}
			else if(devicename.equals(Command.DEVICENAME.FORAP60)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress()); 
			} 
			else if(devicename.equals(Command.DEVICENAME.FORAD40)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress()); 
			}              			
			else if(devicename.equals(Command.DEVICENAME.ML103D)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
			else if(devicename.equals(Command.DEVICENAME.ITONDM)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
			else if(devicename.equals(Command.DEVICENAME.BP)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
			else if(devicename.equals(Command.DEVICENAME.ichoice)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
			else if(devicename.equals(Command.DEVICENAME.BJYC)){
				mNewDevicesArrayAdapter.add(device.getName() + device.getAddress());
			}
		}  
    }
}