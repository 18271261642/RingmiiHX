package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import com.guider.healthring.R;


public class MeasureChoiceFragment extends Fragment{
	private static final String TAG = "MeasureChoiceFragment";
	private static final boolean D = true;
	private View mView;
	private ViewPager mViewpager;
	private SettingsPagerAdapter mAdapter;
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.gd800_measure_viewpager, container, false);
		InitViewPager();
		return mView;
	}
     
    private ArrayList<Fragment> fragmentViews;
	@SuppressLint("InlinedApi")
	private void InitViewPager() {
		fragmentViews = new ArrayList<Fragment>();	 
		fragmentViews.add(new ItemFragment());
		fragmentViews.add(new BluetoothSettingFragment());
		mAdapter = new SettingsPagerAdapter(getChildFragmentManager());
		mViewpager = (ViewPager)mView.findViewById(R.id.pager);
		mViewpager.setAdapter(mAdapter);
		mViewpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pPosition) {
				// TODO Auto-generated method stub
				 
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int index) {
				// TODO Auto-generated method stub
				 
			}
		});		 
    }

	public class SettingsPagerAdapter extends FragmentPagerAdapter {

		public SettingsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentViews.get(position);
		}
		 
		@Override
		public int getCount() {
			return fragmentViews.size();
		}
	}
	
	public static class ItemFragment extends Fragment{
		private ImageView btn_wt;
		private ImageView btn_bo;
		private ImageView btn_bg;
		private ImageView btn_bp;
		private View mView;
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			mView = inflater.inflate(R.layout.gd800_measure_measurechoice, container, false);
			bindButton();
			return mView ; 
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);		
		}

		private void bindButton() {
			btn_wt = (ImageView)mView.findViewById(R.id.btn_wt);
			btn_bp = (ImageView)mView.findViewById(R.id.btn_bp);
			btn_bo = (ImageView)mView.findViewById(R.id.btn_bo);
			btn_bg = (ImageView)mView.findViewById(R.id.btn_bg);							
			btn_wt.setOnClickListener(new OnMeasureClickListener());
			btn_bg.setOnClickListener(new OnMeasureClickListener());
			btn_bo.setOnClickListener(new OnMeasureClickListener());
			btn_bp.setOnClickListener(new OnMeasureClickListener());
		}
		
		private class OnMeasureClickListener implements OnClickListener{

			@Override
			public void onClick(View v) {								    	 
			    Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("BT_TYPE", v.getId());
				intent.putExtras(bundle);
				intent.setClass(getActivity(), BluetoothChatActivity.class); 
				startActivity(intent);			     	
			}	
		}
	} 

	public static class BluetoothSettingFragment extends Fragment{
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
		
		
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        final String action = intent.getAction();
		        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
					mAdapter.notifyDataSetChange();
		        }	       
		    }
		};
		
		private void bindListView() {
			infoList = new ArrayList<Info>();
			infoList.add(new Info(getString(R.string.bluetooth_title_connection)));
			infoList.add(new Info(getString(R.string.bluetooth_device)));		 	 
			mAdapter = new ListViewAdapter();
			list.setAdapter(mAdapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
					// TODO Auto-generated method stub
					if (position == 0){	
						if (!mBluetoothAdapter.isEnabled()) {
				            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				            getActivity().startActivityForResult(enableIntent, BluetoothBaseActivity.REQUEST_ENABLE_BT);
					    }else{
					    	Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
					    	getActivity().startActivityForResult(serverIntent, BluetoothBaseActivity.REQUEST_CONNECT_DEVICE);
					    }
					}					
					else if(position == 1){
//						((LauncherActivity)getActivity()).changeFragment(new MeasureSettingFragment());
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
				
				switcher = new Switch(getActivity());			 
					
				if (position == 0){				
					FLAG_BT = mBluetoothAdapter.isEnabled();
					if(FLAG_BT){
						switcher.setChecked(true);
						mBluetoothAdapter.enable();
					}
					else{
						switcher.setChecked(false);
						mBluetoothAdapter.disable();		
					}
					switcher.setOnCheckedChangeListener(new BTChangeListener());
					switcher.setGravity(Gravity.CENTER_VERTICAL);	
					LinearLayout.LayoutParams lp = 
									new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp.setMargins(0, 10, 5, 10);
					switcher.setLayoutParams(lp);
					((LinearLayout) convertView).addView(switcher);
					
					li.setOnClickListener(new OnClickListener(){
						
						@Override
						public void onClick(View v) {
							if (!mBluetoothAdapter.isEnabled()) {
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
					if(isChecked){
						mBluetoothAdapter.enable();
					}
					else{
						mBluetoothAdapter.disable();		
					}
				}
			}
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
	} 
}