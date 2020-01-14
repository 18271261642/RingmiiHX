package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.guider.healthring.R;

import java.util.ArrayList;

import hat.bemo.BaseFragment;


public class BluetoothSettingFragment extends BaseFragment {
	private View mView;
	private TextView tv_value;
	
	private ListView list;
	private TextView actionbar_tv;
	private ArrayList<Info> infoList;
	private ListViewAdapter mAdapter;
	
	private BluetoothAdapter mBluetoothAdapter;
	private Switch switcher;
	//flag 
	private boolean FLAG_BT ;
	private IntentFilter filter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.gd800_measure_fragment_list, container, false);
		list = (ListView) mView.findViewById(R.id.list);
		actionbar_tv = (TextView) mView.findViewById(R.id.actionbar_title);
		actionbar_tv.setText(R.string.bluetooth_title_connection);
		return mView ;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		FLAG_BT = mBluetoothAdapter.isEnabled();
		bindListView();		
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    filter = new IntentFilter();
	    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	    getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {	 
		super.onDestroy();
		 getActivity().unregisterReceiver(mReceiver);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        final String action = intent.getAction();
	        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){	
	        	Log.e("ACTION_STATE_CHANGED", "藍牙產生變化");
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				FLAG_BT = mBluetoothAdapter.isEnabled();
				mAdapter.notifyDataSetChange();
	        }	       
	    }
	};
	
	private void bindListView() {
		infoList = new ArrayList<Info>();
		infoList.add(new Info(getString(R.string.bluetooth_title_connection)));
		infoList.add(new Info(getString(R.string.bluetooth_device)));
		infoList.add(new Info(getString(R.string.unit)));
		mAdapter = new ListViewAdapter();
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0){						
					if (!FLAG_BT) {
			            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			            getActivity().startActivityForResult(enableIntent, BluetoothBaseActivity.REQUEST_ENABLE_BT);
				    }else{
				    	Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
				    	getActivity().startActivityForResult(serverIntent, BluetoothBaseActivity.REQUEST_CONNECT_DEVICE);
				    }
				}					
				else if(position == 1){
					((BluetoothLauncherActivity)getActivity()).changeFragment(new MeasureSettingFragment());	
				}
				else if(position == 2){
					unitChangeDialog();	
				}
			}
		});
	}
	
	private class ListViewAdapter extends BaseAdapter {
		private LayoutInflater myInflater;

		public ListViewAdapter() {
			myInflater = LayoutInflater.from(getActivity());
		}

		public void notifyDataSetChange(){
			notifyDataSetChanged();
		}	
		
		@Override
		public int getCount() {
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

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {		
			convertView = myInflater.inflate(R.layout.gd800_item_text3, null);
			
			LinearLayout li = (LinearLayout) convertView.findViewById(R.id.li);				
			android.widget.AbsListView.LayoutParams liLp = 
					new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, list.getHeight()/3);
			li.setLayoutParams(liLp);
			
			Info info = infoList.get(position);

			tv_value = (TextView) convertView.findViewById(R.id.tv_value);
			tv_value.setText(info.getValue());
			tv_value.setTextSize(30);
			switcher = new Switch(getActivity());			 
			
			if (position == 0){				
//				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//				FLAG_BT = mBluetoothAdapter.isEnabled();
//				Log.e("FLAG_BT", "FLAG_BT:"+FLAG_BT);
//				if(FLAG_BT){
//					switcher.setChecked(true);
//					mBluetoothAdapter.enable();
//				}
//				else{				
//					switcher.setChecked(false);
//					mBluetoothAdapter.disable();		
//				}
				switcher.setOnCheckedChangeListener(new BTChangeListener());
				switcher.setChecked(FLAG_BT);
				switcher.setGravity(Gravity.CENTER_VERTICAL);	
				LinearLayout.LayoutParams lp = 
								new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 10, 5, 10);
				switcher.setLayoutParams(lp);
				((LinearLayout) convertView).addView(switcher);
				
				li.setOnClickListener(new OnClickListener(){
					
					@Override
					public void onClick(View v) {
						if (!FLAG_BT) {
					            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					            getActivity().startActivityForResult(enableIntent, BluetoothBaseActivity.REQUEST_ENABLE_BT);
					    }else{
					    	Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
					    	getActivity().startActivityForResult(serverIntent, BluetoothBaseActivity.REQUEST_CONNECT_DEVICE);
					    }									
					}	    	
			    });
			}					
			return convertView;
		}
		
		private class BTChangeListener implements OnCheckedChangeListener {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {	
				Log.e("onCheckedChanged", "onCheckedChanged:"+isChecked);
				if(isChecked){
					mBluetoothAdapter.enable();			
				}
				else{
					mBluetoothAdapter.disable();
				}			
			}			
		}
	}
	
	private void unitChangeDialog(){		 
//		final String[] arrayFruit = new String[] {"mmol/L", "mg/dl"};
//		final TextView textView = new TextView(getActivity());
//		textView.setInputType(InputType.TYPE_CLASS_NUMBER);
//		textView.setGravity(Gravity.CENTER);
//		AlertDialog.Builder buider = new AlertDialog.Builder(getActivity()).
//
//		setTitle(R.string.unit).setSingleChoiceItems(arrayFruit, SharedPreferences_status.getUnit(getActivity()),
//				new DialogInterface.OnClickListener() {
//
//					@Override
//		            public void onClick(DialogInterface dialog, int which) {
//		              switch(which){
//		              	 case 0:
//		              		 SharedPreferences_status.setUnit(getActivity(), 0);
//		            	  break;
//		              	 case 1:
//		              		 SharedPreferences_status.setUnit(getActivity(), 1);
//		              	  break;
//
//		              }
//		            }
//		        })
//		.setPositiveButton(getString(R.string.settings_dialog_complete), new DialogInterface.OnClickListener() {
//
//			@Override
//		    public void onClick(DialogInterface arg0, int arg1) {
//
//		     }
//		    });
//		buider.show();
	}
	
	public class Info {
		public String value;
		
		public Info(String value) {			
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}

		@SuppressWarnings("unused")
		public void setValue(String value) {
			this.value = value;
		}
	}

	@Override
	public void setTextSize() {
//		String language = MyApplication.getLanguageEnv();
//		if(language.equals("en")){
//			MyApplication.setTextSize(actionbar_tv, TextSize.XXL);
//		}else{
//			MyApplication.setTextSize(actionbar_tv, TextSize.XXL);
//		}
	}

	@Override
	protected void LoadData() {
		// TODO Auto-generated method stub
		
	}
}