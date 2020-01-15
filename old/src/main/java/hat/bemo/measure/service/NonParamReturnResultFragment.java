package hat.bemo.measure.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import hat.bemo.BaseFragment;
import com.guider.healthring.R;
import hat.bemo.measure.set.Measure_SharedPreferences;
import hat.bemo.setting.Settings;

public class NonParamReturnResultFragment extends BaseFragment {
	private View view;
	private int Page;
	private int layout;
	private int indexpage1, indexpage2, indexpage3, indexpage4, indexpage5;
	private TextView one, two, three;
	private TextView time, diet, other;
	private ImageButton yes, no;
	private ImageButton onebtn, twobtn, threebtn;
	private int bttype;
	/**
	 * page1: 0.起床 
	 * 		  1.餐前
	 * 		  2.餐後
	 * 		  3.就寢
	 * 		  4.沒資料
	 * **/
	private int[] page1 = {R.string.mea_get_up, R.string.mea_fasting, R.string.mea_Postprandial, R.string.mea_sleeping,
						   R.string.mea_no_data};
	/**
	 * page2: 0.少量
	 * 		  1.正常
	 * 		  2.過量
	 * 		  3.沒資料
	 * **/
	private int[] page2 = {R.string.mea_Little, R.string.mea_normal, R.string.mea_excess, R.string.mea_no_data};
	/**
	 * page3: 0.正常
	 * 		  1.飲酒
	 * 		  2.沒資料
	 **/
	private int[] page3 = {R.string.mea_normal, R.string.Drinking, R.string.mea_no_data};
	/**
	 * page4: 0.正常
	 * 		  1.發燒
	 * 		  2.沒資料
	 **/
	private int[] page4 = {R.string.mea_normal, R.string.fever, R.string.mea_no_data};
	/**
	 * page5: [0.1],  
	 * 		  [1.2],
	 * 		  [2.3]
	 * **/
	private String[] page5 = {"1", "2", "3"};
	private String[] enPage5 = {"one", "two", "three"};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Page = getArguments().getInt(Settings.Bundle_Non_Settings_Page);
		bttype = getArguments().getInt("BT_TYPE");	
		Log.e("onCreateView", "bttype:"+bttype);
		view = inflater.inflate(setLayout(Page), null);  		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);		
		findViews(Page);		
	}
	
	private int setLayout(int Page){
		switch (Page) {
		case 0:
//			layout = R.layout.gd800_measure_page5;
			break;
		case 1:
//			layout = R.layout.gd800_measure_page6;
			break;
		case 2:
//			layout = R.layout.gd800_measure_page6;
			break;
		default:
			break;
		}
		return layout;
	}
	
	private void findViews(int Page){
//		switch (Page) {
//		case 0:
//			one = (TextView)view.findViewById(R.id.one);
//			two = (TextView)view.findViewById(R.id.two);
//			three = (TextView)view.findViewById(R.id.three);
//			onebtn = (ImageButton)view.findViewById(R.id.onebtn);
//			twobtn = (ImageButton)view.findViewById(R.id.twobtn);
//			threebtn = (ImageButton)view.findViewById(R.id.threebtn);
//			one.setText(page5[0]);
//			two.setText(page5[1]);
//			three.setText(page5[2]);
//			onebtn.setOnClickListener(new OnClick());
//			twobtn.setOnClickListener(new OnClick());
//			threebtn.setOnClickListener(new OnClick());
//			indexpage5 = Measure_SharedPreferences.getValue_Page5(getActivity());
//			checkView(Page);
//			break;
//		case 1:
//			time = (TextView)view.findViewById(R.id.time);
//			diet = (TextView)view.findViewById(R.id.diet);
//			other = (TextView)view.findViewById(R.id.other);
//			yes = (ImageButton)view.findViewById(R.id.yes);
//			no = (ImageButton)view.findViewById(R.id.no);
//			yes.setOnClickListener(new OnClick());
//			no.setOnClickListener(new OnClick());
//			setSettingsValue();
//			break;
//		case 2:
//			time = (TextView)view.findViewById(R.id.time);
//			diet = (TextView)view.findViewById(R.id.diet);
//			other = (TextView)view.findViewById(R.id.other);
//			yes = (ImageButton)view.findViewById(R.id.yes);
//			no = (ImageButton)view.findViewById(R.id.no);
//			no.setImageResource(R.drawable.selectmeasure_reseat_btn);
//			yes.setOnClickListener(new OnClick());
//			no.setOnClickListener(new OnClick());
//			setSettingsValue();
//			break;
//		default:
//			break;
//		}
	}
	
	private void checkView(int Page){
		if(Page == 0){
			Log.e("indexpage5", "indexpage5:"+indexpage5);
			switch (indexpage5) {
			case 0:					
				onebtn.setSelected(true);
				twobtn.setSelected(false);
				threebtn.setSelected(false);
				break;
			case 1:		
				onebtn.setSelected(false);
				twobtn.setSelected(true);
				threebtn.setSelected(false);	
				break;
			case 2:
				onebtn.setSelected(false);
				twobtn.setSelected(false);
				threebtn.setSelected(true);
				break;		 
			default:
				break;
			}
		}		
	}
	
	class OnClick implements OnClickListener{
		@Override
		public void onClick(View v) {
//			Bundle bundle = null;
//			switch (v.getId()) {
//			case R.id.onebtn:
//				onebtn.setSelected(true);
//				twobtn.setSelected(false);
//				threebtn.setSelected(false);
//				Measure_SharedPreferences.setValue_Page5(getActivity(), 0);
//				break;
//			case R.id.twobtn:
//				onebtn.setSelected(false);
//				twobtn.setSelected(true);
//				threebtn.setSelected(false);
//				Measure_SharedPreferences.setValue_Page5(getActivity(), 1);
//				break;
//			case R.id.threebtn:
//				onebtn.setSelected(false);
//				twobtn.setSelected(false);
//				threebtn.setSelected(true);
//				Measure_SharedPreferences.setValue_Page5(getActivity(), 2);
//				break;
//			case R.id.yes:
//				yes.setSelected(true);
//				no.setSelected(false);
//				Measure_SharedPreferences.setValue_Setting(getActivity(), 0);
//				((BaseActivity)getActivity()).clearAllFragment();
//				Intent intent = new Intent();
//				bundle = new Bundle();
//				bundle.putInt("BT_TYPE", bttype);
//				intent.putExtras(bundle);
////				intent.setClass(getActivity(), NonInvasiveBloodGlucoseActivity.class);
//				intent.setClass(getActivity(), BluetoothChatActivity.class);
//				startActivity(intent);
//				break;
//			case R.id.no:
//				yes.setSelected(false);
//				no.setSelected(true);
//				((BaseActivity)getActivity()).clearAllFragment();
//				bundle = new Bundle();
//				bundle.putInt("BT_TYPE", bttype);
//			    ((BaseActivity)BaseActivity.mContext).changeFragment(new NonInvasiveBoodGlucoseFragment(), bundle);
//				break;
//			default:
//				break;
//			}
		}			
	}
	
	private void setSettingsValue(){
		indexpage1 = Measure_SharedPreferences.getValue_Page1(getActivity());
		indexpage2 = Measure_SharedPreferences.getValue_Page2(getActivity());
		indexpage3 = Measure_SharedPreferences.getValue_Page3(getActivity());
		indexpage4 = Measure_SharedPreferences.getValue_Page4(getActivity());
		indexpage5 = Measure_SharedPreferences.getValue_Page5(getActivity());
		Log.e("indexpage1", "indexpage1:"+indexpage1);
		Log.e("indexpage2", "indexpage2:"+indexpage2);
		Log.e("indexpage3", "indexpage3:"+indexpage3);
		Log.e("indexpage4", "indexpage4:"+indexpage4);
		Log.e("indexpage5", "indexpage5:"+indexpage5); 
		
		String add = "+";
		String hour = getString(R.string.mea_hour);
		
		//時段
		if(indexpage1 == 2){
//			if(MyApplication.getLanguageEnv().equals("en"))
//				time.setText(getString(R.string.mea_time) +
//							 getString(page1[indexpage1]) + " " + enPage5[indexpage5] + " " + hour);
//			else
				time.setText(getString(R.string.mea_time) + getString(page1[indexpage1])+page5[indexpage5]+hour);
		}
		else if(indexpage1 == 4){
			time.setText(getString(R.string.mea_no_data));
		}
		else{
			time.setText(getString(R.string.mea_time) + getString(page1[indexpage1]));
		}		
		
		//飲食		
		if(indexpage2 == 3){
			diet.setText(getString(R.string.mea_no_data));
		}
		else{
			diet.setText(getString(R.string.mea_diet) + getString(page2[indexpage2]));
		}
		
		//其他
		if(indexpage3 == 0 & indexpage4 == 0 | indexpage3 == 1 & indexpage4 == 0){
			add = "";
			other.setText(getString(R.string.mea_other) + getString(page3[indexpage3]));
		}		
		else if(indexpage3 == 0 & indexpage4 == 1 | indexpage3 == 2){
			add = "";
			other.setText(getString(R.string.mea_other) + getString(page4[indexpage4]));
		}		
		else{
			other.setText(getString(R.string.mea_other) + getString(page3[indexpage3]) +add+ getString(page4[indexpage4]));
		}
	}
	
	@Override 
	public void setTextSize() {
//		if(MyApplication.getLanguageEnv().equals("en")){
//			switch (Page) {
//			case 0:
//
//				break;
//			case 1:
//				time.setTextSize(24);
//				diet.setTextSize(24);
//				other.setTextSize(24);
//				break;
//			case 2:
//				time.setTextSize(24);
//				diet.setTextSize(24);
//				other.setTextSize(24);
//				break;
//			}
//		}
	}

	@Override
	protected void LoadData() {
		// TODO Auto-generated method stub
		
	}
}