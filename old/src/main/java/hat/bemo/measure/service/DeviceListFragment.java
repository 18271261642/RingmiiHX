package hat.bemo.measure.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hat.bemo.BaseFragment;
import com.guider.healthring.R;
import hat.bemo.measure.setting_db.Delete;
import hat.bemo.measure.setting_db.MeasureDAO;
import hat.bemo.measure.setting_db.Update;
import hat.bemo.measure.setting_db.VO_BG;
import hat.bemo.measure.setting_db.VO_BO;
import hat.bemo.measure.setting_db.VO_BP;
import hat.bemo.measure.setting_db.VO_WT;

public class DeviceListFragment extends BaseFragment {
	private View mView;
	private ListView list;
	private TextView actionbar_title;
	private int DialStage;
	private Context mContext;
	private ArrayList<Info> infoList;
	private ListViewAdapter mAdapter;
	private List<Boolean> listShow;	 
	private Update up;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private MeasureDAO dao = new MeasureDAO();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);	
		mContext = getActivity();
		DialStage = getArguments().getInt(BluetoothBaseActivity.TYPE);
		infoList = new ArrayList<Info>();
		listShow = new ArrayList<Boolean>();
		up = new Update();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.gd800_measure_fragment_list, container, false);
		list = (ListView)mView.findViewById(R.id.list);
		actionbar_title = (TextView)mView.findViewById(R.id.actionbar_title);
		Measure_type();
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		
	}
	
	private void Measure_type(){/*
		if(DialStage == R.id.btn_item01){
			LoData_BP();
		}
		else if(DialStage == R.id.btn_item02){		
			LoData_BO();
		}
		
		else if(DialStage == R.id.btn_item03){
		*/
			LoData_BG();			
			/*
		}
		else if(DialStage == R.id.btn_item04){
			LoData_WT();	
		}
		*/
		mAdapter = new ListViewAdapter();
		list.setAdapter(mAdapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {				 
				final CheckedTextView chkItem = (CheckedTextView) v.findViewById(R.id.chkItem);
				final Info info = infoList.get(position);
				listShow.set(position, chkItem.isChecked());
				
				chkItem.setOnClickListener(new OnClickListener() {
					
					@SuppressLint("SimpleDateFormat")
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						 ((CheckedTextView) v).toggle();
						 String Switch = (chkItem.isChecked() == true ? "ON" : "OFF");
						 String[] no = {info.getItemno()};
						 String name = info.getName();
						 String address = info.getAddress();
						 String createdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'GMT'Z").format(new Date());
						 /*
						 if(DialStage == R.id.btn_item01){
							 setBP(Switch, no, name, address, createdate, info);
						 }
						 else if(DialStage == R.id.btn_item02){		
							 setBO(Switch, no, name, address, createdate, info);
						 }
						 else if(DialStage == R.id.btn_item03){
						 */
							 setBG(Switch, no, name, address, createdate, info);	
							 /*
						 }
						 else if(DialStage == R.id.btn_item04){
							 setWT(Switch, no, name, address, createdate, info);
						 }						
						 */
					}
				});
			 }
		});
		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, final int position, long id) {
				// TODO Auto-generated method stub
				final Info info = infoList.get(position);
				String name = info.getName();
				String address = info.getAddress();
				String itemno = info.getItemno();
				DialogUnpair(name, address, itemno, position);
				return false;
			}
		});
	}
	
	private void DialogUnpair(String name, final String address, final String itemno, final int position){
		try{
			new AlertDialog.Builder(getActivity())
		    .setTitle(R.string.dialog_undevice)
		    .setMessage(name+"\n"+address)
		    .setPositiveButton(R.string.dialog_unpair, new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	try {
		        		device = mBluetoothAdapter.getRemoteDevice(address);	
						BluetoothChatActivity.removeBond(device.getClass(), device);
						
						Delete del = new Delete();		
						/*
						if(DialStage == R.id.btn_item01){
							del.Delete_BP(mContext, itemno);
						}
						else if(DialStage == R.id.btn_item02){		
							del.Delete_BO(mContext, itemno);	
						}
						else if(DialStage == R.id.btn_item03){
						*/
							del.Delete_BG(mContext, itemno);	
							/*
						}
						else if(DialStage == R.id.btn_item04){
							 
						}
						*/
						infoList.remove(position);
						mAdapter.notifyDataSetChange();					
					} catch (Exception e) {
						e.printStackTrace();
					}
		            dialog.cancel();
		        }
		    })
		    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	 dialog.cancel();
		        }
		    })		    
		    .show();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setBP(String Switch, String[] no, String name, String address, String createdate, Info info){
		 up.up_BP(mContext, no, name+BluetoothBaseActivity.relation+address, createdate, Switch);
			
		 getbp = dao.getdata_BP(getActivity(), "", "");	
		 if(getbp != null){
			for(int i=0; i<getbp.size(); i++){
				VO_BP data = getbp.get(i);
				device_name = data.getDEVICE_NAME();
				Itemno = data.getITEMNO();	
				String[] dname = device_name.split(BluetoothBaseActivity.relation);	
				String[] daddress = device_name.split(BluetoothBaseActivity.relation);							
				if(! (String.valueOf(info.getItemno()).equals(String.valueOf(Itemno)))){
					System.out.println(info.getItemno() +":"+ Itemno);
					String[] number = {Itemno};
					up.up_BP(mContext, number, dname[0]+BluetoothBaseActivity.relation+daddress[1], 
							 createdate, BluetoothBaseActivity.MEAUSE_DEVICE_OFF);
				}else{
					Log.e("",info.getItemno() +":"+ Itemno);
				}
			}
		 }	 
		 LoData_BP();
		 System.out.println(info.getItemno()+":"+Switch);	
		 mAdapter.notifyDataSetChange();	
	}
	
	private void setBG(String Switch, String[] no, String name, String address, String createdate, Info info){
		 up.up_BG(mContext, no, name+BluetoothBaseActivity.relation+address, createdate, Switch);		
		 getbg = dao.getdata_BG(getActivity(), "", "");	
		 if(getbg != null){
			for(int i=0; i<getbg.size(); i++){
				VO_BG data = getbg.get(i);
				device_name = data.getDEVICE_NAME();
				Itemno = data.getITEMNO();	
				String[] dname = device_name.split(BluetoothBaseActivity.relation);	
				String[] daddress = device_name.split(BluetoothBaseActivity.relation);							
				if(! (String.valueOf(info.getItemno()).equals(String.valueOf(Itemno)))){
					System.out.println(info.getItemno() +":"+ Itemno);
					String[] number = {Itemno};
					up.up_BG(mContext, number, dname[0]+BluetoothBaseActivity.relation+daddress[1], 
							 createdate, BluetoothBaseActivity.MEAUSE_DEVICE_OFF);
				}else{
					Log.e("",info.getItemno() +":"+ Itemno);
				}
			}
		 }		 
		 LoData_BG();
		 System.out.println(info.getItemno()+":"+Switch);	
		 mAdapter.notifyDataSetChange();	
	}

	private void setBO(String Switch, String[] no, String name, String address, String createdate, Info info){
		 up.up_BO(mContext, no, name+BluetoothBaseActivity.relation+address, createdate, Switch);
			
		 getbo = dao.getdata_BO(getActivity(), "", "");	
		 if(getbo != null){
			for(int i=0; i<getbo.size(); i++){
				VO_BO data = getbo.get(i);
				device_name = data.getDEVICE_NAME();
				Itemno = data.getITEMNO();	
				String[] dname = device_name.split(BluetoothBaseActivity.relation);	
				String[] daddress = device_name.split(BluetoothBaseActivity.relation);							
				if(! (String.valueOf(info.getItemno()).equals(String.valueOf(Itemno)))){
					System.out.println(info.getItemno() +":"+ Itemno);
					String[] number = {Itemno};
					up.up_BO(mContext, number, dname[0]+BluetoothBaseActivity.relation+daddress[1], 
							 createdate, BluetoothBaseActivity.MEAUSE_DEVICE_OFF);
				}
				else{
					Log.e("",info.getItemno() +":"+ Itemno);
				}
			}
		 }	 
		 LoData_BO();
		 System.out.println(info.getItemno()+":"+Switch);	
		 mAdapter.notifyDataSetChange();	
	}
	
	private void setWT(String Switch, String[] no, String name, String address, String createdate, Info info){
		 up.up_WT(mContext, no, name+BluetoothBaseActivity.relation+address, createdate, Switch);
			
		 getwt = dao.getdata_WT(getActivity(), "", "");	
		 if(getwt != null){
			for(int i=0; i<getwt.size(); i++){
				VO_WT data = getwt.get(i);
				device_name = data.getDEVICE_NAME();
				Itemno = data.getITEMNO();	
				String[] dname = device_name.split(BluetoothBaseActivity.relation);	
				String[] daddress = device_name.split(BluetoothBaseActivity.relation);							
				if(! (String.valueOf(info.getItemno()).equals(String.valueOf(Itemno)))){
					System.out.println(info.getItemno() +":"+ Itemno);
					String[] number = {Itemno};
					up.up_WT(mContext, number, dname[0]+BluetoothBaseActivity.relation+daddress[1], 
							 createdate, BluetoothBaseActivity.MEAUSE_DEVICE_OFF);
				}else{
					Log.e("",info.getItemno() +":"+ Itemno);
				}
			}
		 }	 
		 LoData_WT();
		 System.out.println(info.getItemno()+":"+Switch);	
		 mAdapter.notifyDataSetChange();	
	}
	
    private String[] name;
    private String[] address;
    private String device_name;
    private String Switch;
    private String Itemno;
    private ArrayList<VO_BP> getbp;
    private ArrayList<VO_BO> getbo;
    private ArrayList<VO_BG> getbg;
    private ArrayList<VO_WT> getwt;
    
	private void LoData_BP(){
		if(infoList != null)
		infoList.clear();
		if(listShow != null)
		listShow.clear();
		
		getbp = dao.getdata_BP(getActivity(), "", "");	
		if(getbp != null){
			for(int i=0; i<getbp.size(); i++){
				VO_BP data = getbp.get(i);
				device_name = data.getDEVICE_NAME();
				Switch = data.getSWITCH();
				Itemno = data.getITEMNO();
				
				name = device_name.split(BluetoothBaseActivity.relation);	
				address = device_name.split(BluetoothBaseActivity.relation);
				boolean b = (Switch.equals("OFF") ? false : true);
				infoList.add(new Info(name[0], address[1], Itemno, b));
				listShow.add(b);
			}
		}
		actionbar_title.setText(getString(R.string.bluetooth_device_bp));
	}
	
	private void LoData_BO(){
		if(infoList != null)
		infoList.clear();
		if(listShow != null)
		listShow.clear();
		
		getbo = dao.getdata_BO(getActivity(), "", "");	
		if(getbo != null){
			for(int i=0; i<getbo.size(); i++){
				VO_BO data = getbo.get(i);
				device_name = data.getDEVICE_NAME();
				Switch = data.getSWITCH();
				Itemno = data.getITEMNO();
				
				name = device_name.split(BluetoothBaseActivity.relation);	
				address = device_name.split(BluetoothBaseActivity.relation);
				boolean b = (Switch.equals("OFF") ? false : true);
				infoList.add(new Info(name[0], address[1], Itemno, b));
				listShow.add(b);
			}
		}
		actionbar_title.setText(getString(R.string.bluetooth_device_bg));
	}
	
	private void LoData_BG(){
		if(infoList != null)
		infoList.clear();
		if(listShow != null)
		listShow.clear();
		
		getbg = dao.getdata_BG(getActivity(), "", "");	
		if(getbg != null){
			for(int i=0; i<getbg.size(); i++){
				VO_BG data = getbg.get(i);
				device_name = data.getDEVICE_NAME();
				Switch = data.getSWITCH();
				Itemno = data.getITEMNO();
				
				name = device_name.split(BluetoothBaseActivity.relation);	
				address = device_name.split(BluetoothBaseActivity.relation);
				boolean b = (Switch.equals("OFF") ? false : true);
				infoList.add(new Info(name[0], address[1], Itemno, b));
				listShow.add(b);
			}
		}
		actionbar_title.setText(getString(R.string.bluetooth_device_bo));
	}
	
	private void LoData_WT(){
		if(infoList != null)
		infoList.clear();
		if(listShow != null)
		listShow.clear();
		
		getwt = dao.getdata_WT(getActivity(), "", "");	
		if(getwt != null){
			for(int i=0; i<getwt.size(); i++){
				VO_WT data = getwt.get(i);
				device_name = data.getDEVICE_NAME();
				Switch = data.getSWITCH();
				Itemno = data.getITEMNO();
				
				name = device_name.split(BluetoothBaseActivity.relation);	
				address = device_name.split(BluetoothBaseActivity.relation);
				boolean b = (Switch.equals("OFF") ? false : true);
				infoList.add(new Info(name[0], address[1], Itemno, b));
				listShow.add(b);
			}
		}
		actionbar_title.setText(getString(R.string.bluetooth_device_wt));
	}
	
	private class ListViewAdapter extends BaseAdapter {
		private LayoutInflater myInflater;

		public ListViewAdapter() {
			myInflater = LayoutInflater.from(mContext);
		}
		
		public void notifyDataSetChange(){
			notifyDataSetChanged();
		}	
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return infoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;					
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = myInflater.inflate(R.layout.gd800_measure_list, null);
				viewHolder.tv_value = (TextView) convertView.findViewById(R.id.tv_value);
				viewHolder.chkItem = (CheckedTextView) convertView.findViewById(R.id.chkItem);
				viewHolder.li = (LinearLayout) convertView.findViewById(R.id.li);				
				android.widget.AbsListView.LayoutParams liLp = 
								new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, list.getHeight()/3);
				viewHolder.li.setLayoutParams(liLp);
				
				convertView.setTag(viewHolder);
			}
			else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Info info = infoList.get(position);
			viewHolder.tv_value.setText(info.getName()+"\n"+info.getAddress());	
			viewHolder.tv_value.setTextSize(22);
			viewHolder.chkItem.setChecked(info.getSwitch());			
			return convertView;
		}
	}
	 
	@Override
	public void onStop() {
		super.onStop();
		 for(int x=0 ; x<listShow.size() ; x++) {
	         Log.e("","listShow("+x+") is "+listShow.get(x));
	         if(listShow.get(x) == true){
	        	 
	         }
		 }
	}
	
	private static class ViewHolder{
		private TextView tv_value;
		private CheckedTextView chkItem;
		private LinearLayout li;
	}
	
	public class Info {
		public String name;
		public String address;
		public String itemno;
		public boolean Switch;
		
		public Info(String name, String address, String itemno, boolean Switch) {
			this.name = name;		
			this.address = address;	
			this.itemno = itemno;
			this.Switch = Switch;	
		}
		
		public String getName() {
			return name;
		}

		public void setName(String value) {
			this.name = value;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getItemno() {
			return itemno;
		}

		public void setItemno(String itemno) {
			this.itemno = itemno;
		}

		public boolean getSwitch() {
			return Switch;
		}

		public void setSwitch(boolean switch1) {
			Switch = switch1;
		}		
	}

	@Override
	public void setTextSize() {
//		String language = MyApplication.getLanguageEnv();
//		if(language.equals("en")){
//			MyApplication.setTextSize(actionbar_title, TextSize.XXL);
//		}
//		else{
//			MyApplication.setTextSize(actionbar_title, TextSize.XXL);
//		}
	}

	@Override
	protected void LoadData() {
		// TODO Auto-generated method stub
		
	}
}