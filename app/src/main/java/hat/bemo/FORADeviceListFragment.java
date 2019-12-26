package hat.bemo;

import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hat.bemo.measure.database.Delete;
import hat.bemo.measure.database.MeasureBLEDAO;
import hat.bemo.measure.database.MeasureDevice;
import hat.bemo.measure.database.MeasureRecord;
import hat.bemo.measure.database.VOMeasureDevice;
import hat.bemo.measure.database.VOMeasureRecord;
import com.guider.healthring.R;

public class FORADeviceListFragment extends BaseFragment {
	private ArrayList<Info> infoList;
	private ListViewAdapter mAdapter;
	private ListView devicelist;
	private TextView mnodata;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
		mView = inflater.inflate(R.layout.fora_device_list, container, false);		
		return mView; 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		findViews();
		init();
		onClick();		
	}
	
	private void findViews() {
		devicelist = (ListView) mView.findViewById(R.id.devicelist);	
		mnodata = (TextView)mView.findViewById(R.id.normaltxt);
		mnodata.setVisibility(View.VISIBLE);
		devicelist.setBackgroundResource(R.drawable.fora_bg_a0);
	}
	
	private void init() {
		infoList = new ArrayList<Info>();
		mAdapter = new ListViewAdapter();
		getMeasureDevice();
		getMeasureRecord();
		devicelist.setAdapter(mAdapter);
	}
	
	private void onClick() {
		devicelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
				setAskDeleteDialog(position);
			}
		});
	}
	
	private void setAskDeleteDialog(final int position) {
		final Dialog dialog = new Dialog(mContext, R.style.AppTheme);
		dialog.setContentView(R.layout.fora_device_list_delete_dialog);
		TextView deletetext = (TextView) dialog.findViewById(R.id.deletetext);
		RelativeLayout relativeLayoutDialog = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutDialog);
		deletetext.setText(infoList.get(position).getDeviceName() + "?");				
		View deleteYes = (View) dialog.findViewById(R.id.deleteYes);
		View deleteNo = (View) dialog.findViewById(R.id.deleteNo);
		
		/**
		 * 硬體設備型號
		 * prams 800 800H
		 */
//		if(UserInfoFragment.HARDWARE_VERSION.equals("800")){
//
//		}
//		else{
			relativeLayoutDialog.setPadding(10, 10, 10, 10);
//		}
		
		deleteYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeList(position);
				dialog.cancel();
			}
		});		
		
		deleteNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});		
		dialog.show();
	}
	
	private void removeList(int position) {
		Delete mDelete = new Delete();
		mDelete.deleteMeasureDevice(getActivity(), infoList.get(position).getItemno());
		infoList.remove(infoList.get(position));
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
				convertView = myInflater.inflate(R.layout.fora_device_list_item, null);
				viewHolder.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
				viewHolder.devicelistlayout = (LinearLayout) convertView.findViewById(R.id.devicelistlayout);
				
				/**
				 * 硬體設備型號
				 * prams 800 800H
				 */
//				if(UserInfoFragment.HARDWARE_VERSION.equals("800")){
//
//				}
//				else{
					viewHolder.devicelistlayout.setPadding(10, 10, 10, 10);
//				}
				
				android.widget.AbsListView.LayoutParams liLp = 
						new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, devicelist.getHeight() / 4);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Info info = infoList.get(position);
			viewHolder.deviceName.setText(info.getDeviceName());							
			return convertView;
		}
	}
	
	public class Info {
		private String deviceName;
		private String deviceAddress;
		private String itemno;
		public Info(String deviceName, String deviceAddress, String itemno) {
			this.deviceName = deviceName;	
			this.deviceAddress = deviceAddress;	
			this.itemno = itemno;	
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

		public String getItemno() {
			return itemno;
		}

		public void setItemno(String itemno) {
			this.itemno = itemno;
		}
	}
	
	private static class ViewHolder{
		private TextView deviceName;	
		private LinearLayout devicelistlayout;	
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
    private String device_name;
    private void getMeasureDevice() {
    	MeasureBLEDAO dao = new  MeasureBLEDAO();
		ArrayList<VOMeasureDevice> getmeasure = dao.getMeasureDevice(getActivity(), null);
		if(getmeasure != null){
			for(int i=0; i<getmeasure.size(); i++){
				VOMeasureDevice data = getmeasure.get(i);
				Log.e("Table_name","Table_name="+ MeasureDevice.TABLE_NAME);
				System.out.println("getItemno="+data.getItemno());
				System.out.println("getDevice="+data.getDevice());
				System.out.println("getCreateDate="+data.getCreateDate());		
				System.out.println("---------------------------");
				device_name = data.getDevice();
				name = device_name.split("/");	
				address = device_name.split("/");	
				infoList.add(new Info(name[0], address[1], data.getItemno()));
			}
			devicelist.setBackgroundResource(R.drawable.fora_bg_a1);
			mnodata.setVisibility(View.INVISIBLE);
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
	}

}