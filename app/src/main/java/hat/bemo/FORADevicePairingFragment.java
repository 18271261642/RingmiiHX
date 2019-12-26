package hat.bemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hat.bemo.BlueTooth.blegatt.baseService.BLEScanService;
import hat.bemo.measure.database.Insert;
import hat.bemo.measure.database.MeasureBLEDAO;
import hat.bemo.measure.database.MeasureDevice;
import hat.bemo.measure.database.MeasureRecord;
import hat.bemo.measure.database.VOMeasureDevice;
import hat.bemo.measure.database.VOMeasureRecord;
import com.guider.healthring.R;

public class FORADevicePairingFragment extends BaseFragment {
	private LinearLayout linearLayoutPair;
	private BluetoothAdapter mBtAdapter;
	private BroadcastReceiver mReceiver = null;
	private ArrayList<String> mCheckDeviceNameList;
	private ArrayList<Info> infoList;
	private ListViewAdapter mAdapter;
	private ListView nameList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
		mView = inflater.inflate(R.layout.fora_device_pairing, container, false);		
		
		return mView; 
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		findViews();
		onClick();
		startBroadcastReceiver();
		initRegisterReceiver();
		init();	
	}
	
	private void findViews() {
		nameList = (ListView) mView.findViewById(R.id.nameList);	
		nameList.setBackgroundResource(R.drawable.fora_bg_a1);
	}
	
	private void init() {
		getMeasureDevice();
		getMeasureRecord();
		nameList.setAdapter(mAdapter);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mBtAdapter.startDiscovery();
	}
	
	private void onClick() {
		nameList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
				setAskPairingDialog(position);
			}
		});
	}
	
	private void setAskPairingDialog(final int position) {
		final Dialog dialog = new Dialog(mContext, R.style.AppTheme);
		dialog.setContentView(R.layout.fora_device_list_pairing_dialog); 
		TextView pairingtext = (TextView) dialog.findViewById(R.id.pairingtext);
		View pairingYes = (View) dialog.findViewById(R.id.pairingYes);
		View pairingNo = (View) dialog.findViewById(R.id.pairingNo);
		pairingtext.setText(infoList.get(position).getDeviceName() + "?");
		
		pairingYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				devicePairing(position);
				dialog.cancel();
			}
		});		
		
		pairingNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});		
		dialog.show();
	}
	
	private void devicePairing(int position) {
		Insert mInsert = new Insert();
		VOMeasureDevice mVOMeasureDevice = new VOMeasureDevice();
		mVOMeasureDevice.setDevice(infoList.get(position).getDeviceName() + "/" + infoList.get(position).getDeviceAddress());
		mVOMeasureDevice.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
		mInsert.insertMeasureDevice(getActivity(), mVOMeasureDevice);
		infoList.remove(position);
		startNotifyDataSetChanged();
		
		Intent mIntent = new Intent(getActivity(), BLEScanService.class);
		getActivity().startService(mIntent);
	}
	
	private void initRegisterReceiver() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
	}
	
	private void cancelRegisterReceiver() {
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
			getActivity().unregisterReceiver(mReceiver);
		}
	}
	
	private void startBroadcastReceiver() {
		mCheckDeviceNameList = new ArrayList<String>();
		infoList = new ArrayList<Info>();
		mAdapter = new ListViewAdapter();
		
	    mReceiver = new BroadcastReceiver() {
	        @SuppressLint("LongLogTag")
			@Override
	        public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				String deviceName = "";
				String deviceAddress = "";
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					Log.e("FORADevicePairingFragment", "Scan Device...");
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					
					if (device == null)
						return;					
//					if (!filterDeviceID(device.getName()))
//						return;					
					if (mCheckDeviceNameList.indexOf(device.getName()) == 0 | mCheckDeviceNameList.indexOf(device.getName()) == 1) 
						return;
						
					//Log.e("FORADevicePairingFragment", device.getName());						
					deviceName = device.getName();		
					//Mark 不要有null的東西~!!
					if(deviceName == null){
						return;
					}
					deviceAddress = device.getAddress();
					mCheckDeviceNameList.add(deviceName);
					checkDeviceList(deviceName, deviceAddress);		
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//					Log.e("mReceiver", "掃描結束");
				}
	        }
	    };
	}
	
	private static boolean filterDeviceID(String device) { 
		if(	device.contains("FORA")){
			return true;
		}
		return false;
	}
	
	private void checkDeviceList(String mDeviceName, String mDeviceAddress) {
		getPairDevice(mDeviceName + "/" + mDeviceAddress);
		if (getmeasure == null)
			infoList.add(new Info(mDeviceName, mDeviceAddress));	
		else
			return;
		
		startNotifyDataSetChanged();
	}
	
	private void startNotifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}
	
	private class ListViewAdapter extends BaseAdapter {

		private LayoutInflater myInflater;

		public ListViewAdapter() {
			myInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			System.out.println(infoList.size());
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			return infoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = myInflater.inflate(R.layout.fora_device_pairing_item, null);
				viewHolder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
				android.widget.AbsListView.LayoutParams liLp = 
						new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, nameList.getHeight() / 4);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Info info = infoList.get(position);
			viewHolder.deviceName.setText(info.getDeviceName() + "\n" + info.getDeviceAddress());							
			return convertView;
		}
	}
	
	public class Info {
		private String deviceName;
		private String deviceAddress;
		public Info(String deviceName, String deviceAddress) {
			this.deviceName = deviceName;	
			this.deviceAddress = deviceAddress;	
		}
		
		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getDeviceAddress() {
			return deviceAddress;
		}

		public void setDeviceAddress(String deviceAddress) {
			this.deviceAddress = deviceAddress;
		}			
	}
	
	private static class ViewHolder{
		private TextView deviceName;		 
	}
	
	@Override
	public void setTextSize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void LoadData() {
		
	} 
	
    private String[] name;
    private String[] address;
    private String deviceName;
    private ArrayList<VOMeasureDevice> getmeasure;
    private void getMeasureDevice() {
    	MeasureBLEDAO dao = new  MeasureBLEDAO();
		getmeasure = dao.getMeasureDevice(getActivity(), null);
		if(getmeasure != null) {
			for(int i=0; i<getmeasure.size(); i++) {
				VOMeasureDevice data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+ MeasureDevice.TABLE_NAME);
				System.out.println("getItemno="+data.getItemno());
				System.out.println("getDevice="+data.getDevice());
				System.out.println("getCreateDate="+data.getCreateDate());		
				System.out.println("---------------------------");
				deviceName = data.getDevice();
				name = deviceName.split("/");	
				address = deviceName.split("/");	
			}
		}
		else{
//			Log.e("FORADevicePairingFragment", "DB Data Null");
		}
    }
 
    private void getPairDevice(String deviceName) {
    	MeasureBLEDAO dao = new  MeasureBLEDAO();
		getmeasure = dao.getMeasureDevice(getActivity(), deviceName);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++) {
				VOMeasureDevice data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+MeasureDevice.TABLE_NAME);
				System.out.println("getItemno="+data.getItemno());
				System.out.println("getDevice="+data.getDevice());
				System.out.println("getCreateDate="+data.getCreateDate());		
				System.out.println("---------------------------");
				deviceName = data.getDevice();
				name = deviceName.split("/");	
				address = deviceName.split("/");	
			}
		}
		else{
//			Log.e("FORADevicePairingFragment", "DB Data Null");
		}
    }
    
    private void getMeasureRecord() {
    	MeasureBLEDAO dao = new  MeasureBLEDAO();
		ArrayList<VOMeasureRecord> getmeasure = dao.getMeasureRecord(getActivity());
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VOMeasureRecord data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+ MeasureRecord.TABLE_NAME);
				System.out.println("getItemno="+data.getItemno());
				System.out.println("getMeasureType="+data.getMeasureType());
				System.out.println("getValue="+data.getValue());
				System.out.println("getCreateDate="+data.getCreateDate());		
				System.out.println("---------------------------");		
			}
		}
    }
   
	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelRegisterReceiver();
	}
}