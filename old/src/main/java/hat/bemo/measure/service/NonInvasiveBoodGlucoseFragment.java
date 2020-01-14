package hat.bemo.measure.service;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import hat.bemo.BaseFragment;
import com.guider.healthring.R;

public class NonInvasiveBoodGlucoseFragment extends BaseFragment {
	private ViewPager mViewpager;
	private SettingsPagerAdapter mAdapter;
	private static final int MAX_FUNCTION_PAGE_SIZE = 4;
	private ArrayList<Fragment> fragmentViews;
	private boolean mIsChanged = false;
	private int mCurrentPagePosition = 0;
	private ViewGroup group;
	private View view;
	private static int bttype;
	private ImageView[] imageViews_group;
	
	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	NoticeDialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.gd800_measure_nondialog, null);
//		Log.e("NonInvasiveBoodGlucoseFragment", "onCreateView");
		bttype = getArguments().getInt("BT_TYPE");	
		mViewpager = (ViewPager)view.findViewById(R.id.nonpager);
		group = (ViewGroup) view.findViewById(R.id.viewgroup);
//		InitViewPager();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
//		Log.e("NonInvasiveBoodGlucoseFragment", "onActivityCreated");
	}
		
	private void setAdapter(){	
		imageViews_group = new ImageView[MAX_FUNCTION_PAGE_SIZE];
		/**
		 * 有幾張圖片 下面就顯示幾個小圓點
		 */
		for (int i = 0; i < imageViews_group.length; i++) {
			LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
												   LinearLayout.LayoutParams.WRAP_CONTENT,
												   LinearLayout.LayoutParams.WRAP_CONTENT);
			// 設置每個小圓點距離左邊的間距
			margin.setMargins(10, 0, 0, 0);
			ImageView imageView = new ImageView(getActivity());
			// 設置每個小圓點的寬高
			imageView.setLayoutParams(new LayoutParams(15, 15));
			imageViews_group[i] = imageView;
			if (i == 0) {
				// 默認選中第一張圖片
//				imageViews_group[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				// 其他圖片都設置未選中狀態
//				imageViews_group[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageViews_group[i], margin);											
		}				
	}
	
//	private void InitViewPager() {
//		setAdapter();
//		fragmentViews = new ArrayList<Fragment>();
//		ItemFragment fragment;
//		Bundle b;
//		if(MAX_FUNCTION_PAGE_SIZE > 0){
//			for (int i = 0; i < MAX_FUNCTION_PAGE_SIZE; i++) {
//				fragment = new ItemFragment();
//				b = new Bundle();
//				b.putInt(Settings.Bundle_Settings_Page, i);
//				fragment.setArguments(b);
//				if(i == MAX_FUNCTION_PAGE_SIZE-1){
//					fragment = new ItemFragment();
//					b = new Bundle();
//					b.putInt(Settings.Bundle_Settings_Page, i);
//					fragment.setArguments(b);
//					fragmentViews.add(fragment);
//				}else{
//					fragment = new ItemFragment();
//					b = new Bundle();
//					b.putInt(Settings.Bundle_Settings_Page, i);
//					fragment.setArguments(b);
//					fragmentViews.add(fragment);
//				}
//			}
//		}
//		mAdapter = new SettingsPagerAdapter(getChildFragmentManager());
//		mViewpager.setAdapter(mAdapter);
//		mViewpager.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int Position) {
//				int	count = imageViews_group.length;
//				for (int i = 0; i < count; i++) {
//					if(Position != i){
////						imageViews_group[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
//					}
//					else{
////						imageViews_group[i].setBackgroundResource(R.drawable.page_indicator_focused);
//					}
//				}
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int index) {
//				// TODO Auto-generated method stub
//			}
//		});
//		mViewpager.setCurrentItem(mCurrentPagePosition, false);
//		mViewpager.setOffscreenPageLimit(200);
//		mViewpager.setOverScrollMode(View.OVER_SCROLL_NEVER);
//    }
	
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
			//
			return fragmentViews.size();
		}
	}  
	
//	public static class ItemFragment extends BaseFragment{
//		private View mView;
//		private int Page;
//		private Context mcontext;
//		private ImageButton sleep, after, before, getup;
//		private ImageButton Little, normal, excess, yes, no;
//		private TextView Measurement_period_text, get_up_text, fasting_text, Postprandial_text, sleeping_text;
//		private TextView eat_status_text, Little_text, normal_text, excess_text, yes_text, no_text, Drinking_text, fever_text;
//		private int layout;
//		private int indexpage1, indexpage2, indexpage3, indexpage4, indexpage5;
//
//		@Override
//		public void onAttach(Activity activity) {
//			super.onAttach(activity);
//			Page = getArguments().getInt(Settings.Bundle_Settings_Page);
//			mcontext = getActivity();
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			mView = inflater.inflate(setLayout(Page), container, false);
//			return mView;
//		}
//
//		@Override
//		public void onActivityCreated(Bundle savedInstanceState) {
//			super.onActivityCreated(savedInstanceState);
//			indexpage1 = Measure_SharedPreferences.getValue_Page1(getActivity());
//			indexpage2 = Measure_SharedPreferences.getValue_Page2(getActivity());
//			indexpage3 = Measure_SharedPreferences.getValue_Page3(getActivity());
//			indexpage4 = Measure_SharedPreferences.getValue_Page4(getActivity());
//			findViews(Page);
//			checkView(Page);
//		}
//
//		private int setLayout(int Page){
//			switch (Page) {
//			case 0:
//				layout = R.layout.gd800_measure_page1;
//				break;
//			case 1:
//				layout = R.layout.gd800_measure_page2;
//				break;
//			case 2:
//				layout = R.layout.gd800_measure_page3;
//				break;
//			case 3:
//				layout = R.layout.gd800_measure_page4;
//				break;
//			default:
//				break;
//			}
//			return layout;
//		}
//
//		private void findViews(int Page){
//			switch (Page) {
//			case 0:
//				sleep = (ImageButton)mView.findViewById(R.id.sleep);
//				after = (ImageButton)mView.findViewById(R.id.after);
//				before = (ImageButton)mView.findViewById(R.id.before);
//				getup = (ImageButton)mView.findViewById(R.id.getup);
//				Measurement_period_text = (TextView)mView.findViewById(R.id.Measurement_period_text);
//				get_up_text = (TextView)mView.findViewById(R.id.get_up_text);
//				fasting_text = (TextView)mView.findViewById(R.id.fasting_text);
//				Postprandial_text = (TextView)mView.findViewById(R.id.Postprandial_text);
//				sleeping_text = (TextView)mView.findViewById(R.id.sleeping_text);
//				sleep.setOnClickListener(new OnClick());
//				after.setOnClickListener(new OnClick());
//				before.setOnClickListener(new OnClick());
//				getup.setOnClickListener(new OnClick());
//				break;
//			case 1:
//				Little = (ImageButton)mView.findViewById(R.id.Little);
//				normal = (ImageButton)mView.findViewById(R.id.normal);
//				excess = (ImageButton)mView.findViewById(R.id.excess);
//				eat_status_text = (TextView)mView.findViewById(R.id.eat_status_text);
//				Little_text = (TextView)mView.findViewById(R.id.Little_text);
//				normal_text = (TextView)mView.findViewById(R.id.normal_text);
//				excess_text = (TextView)mView.findViewById(R.id.excess_text);
//				Little.setOnClickListener(new OnClick());
//				normal.setOnClickListener(new OnClick());
//				excess.setOnClickListener(new OnClick());
//				break;
//			case 2:
//				yes = (ImageButton)mView.findViewById(R.id.yes);
//				no = (ImageButton)mView.findViewById(R.id.no);
//				yes_text = (TextView)mView.findViewById(R.id.yes_text);
//				no_text = (TextView)mView.findViewById(R.id.no_text);
//				Drinking_text = (TextView)mView.findViewById(R.id.Drinking_text);
//				yes.setOnClickListener(new OnClick());
//				no.setOnClickListener(new OnClick());
//				break;
//			case 3:
//				yes = (ImageButton)mView.findViewById(R.id.yes);
//				no = (ImageButton)mView.findViewById(R.id.no);
//				yes_text = (TextView)mView.findViewById(R.id.yes_text);
//				no_text = (TextView)mView.findViewById(R.id.no_text);
//				fever_text = (TextView)mView.findViewById(R.id.fever_text);
//				yes.setOnClickListener(new OnClick());
//				no.setOnClickListener(new OnClick());
//				break;
//			default:
//				break;
//			}
//		}
//
//		private void checkView(int Page){
//			switch (Page) {
//			case 0:
//				switch (indexpage1) {
//				case 0:
//					getup.setSelected(true);
//					before.setSelected(false);
//					after.setSelected(false);
//					sleep.setSelected(false);
//					break;
//				case 1:
//					getup.setSelected(false);
//					before.setSelected(true);
//					after.setSelected(false);
//					sleep.setSelected(false);
//					break;
//				case 2:
//					getup.setSelected(false);
//					before.setSelected(false);
//					after.setSelected(true);
//					sleep.setSelected(false);
//					break;
//				case 3:
//					getup.setSelected(false);
//					before.setSelected(false);
//					after.setSelected(false);
//					sleep.setSelected(true);
//					break;
//				default:
//					break;
//				}
//				break;
//			case 1:
//				switch (indexpage2) {
//				case 0:
//					Little.setSelected(true);
//					normal.setSelected(false);
//					excess.setSelected(false);
//					break;
//				case 1:
//					Little.setSelected(false);
//					normal.setSelected(true);
//					excess.setSelected(false);
//					break;
//				case 2:
//					Little.setSelected(false);
//					normal.setSelected(false);
//					excess.setSelected(true);
//					break;
//				default:
//					break;
//				}
//				break;
//			case 2:
//				switch (indexpage3) {
//				case 0:
//					yes.setSelected(false);
//					no.setSelected(true);
//					break;
//				case 1:
//					yes.setSelected(true);
//					no.setSelected(false);
//					break;
//				default:
//					break;
//				}
//				break;
//			case 3:
//				switch (indexpage4) {
//				case 0:
//					yes.setSelected(false);
//					no.setSelected(true);
//					break;
//				case 1:
//					yes.setSelected(true);
//					no.setSelected(false);
//					break;
//				default:
//					break;
//				}
//				break;
//			default:
//				break;
//			}
//		}
//
//		class OnClick implements OnClickListener{
//			@Override
//			public void onClick(View v) {
////				Bundle b = null;
////				switch (v.getId()) {
////				case R.id.getup:
////					sleep.setSelected(false);
////					after.setSelected(false);
////					before.setSelected(false);
////					getup.setSelected(true);
////					Measure_SharedPreferences.setValue_Page1(mcontext, 0);
////					break;
////				case R.id.before:
////					sleep.setSelected(false);
////					after.setSelected(false);
////					before.setSelected(true);
////					getup.setSelected(false);
////					Measure_SharedPreferences.setValue_Page1(mcontext, 1);
////					break;
////				case R.id.after:
////					sleep.setSelected(false);
////					after.setSelected(true);
////					before.setSelected(false);
////					getup.setSelected(false);
////					Measure_SharedPreferences.setValue_Page1(mcontext, 2);
////					b = new Bundle();
////					b.putInt(Settings.Bundle_Non_Settings_Page, 0);
////					((BaseActivity) getActivity()).changeFragment(new NonParamReturnResultFragment(), b);
////					break;
////				case R.id.sleep:
////					sleep.setSelected(true);
////					after.setSelected(false);
////					before.setSelected(false);
////					getup.setSelected(false);
////					Measure_SharedPreferences.setValue_Page1(mcontext, 3);
////					break;
////				case R.id.Little:
////					Little.setSelected(true);
////					normal.setSelected(false);
////					excess.setSelected(false);
////					Measure_SharedPreferences.setValue_Page2(mcontext, 0);
////					break;
////				case R.id.normal:
////					Little.setSelected(false);
////					normal.setSelected(true);
////					excess.setSelected(false);
////					Measure_SharedPreferences.setValue_Page2(mcontext, 1);
////					break;
////				case R.id.excess:
////					Little.setSelected(false);
////					normal.setSelected(false);
////					excess.setSelected(true);
////					Measure_SharedPreferences.setValue_Page2(mcontext, 2);
////					break;
////				case R.id.no:
////					yes.setSelected(false);
////					no.setSelected(true);
////					if(Page == 3){
////						Measure_SharedPreferences.setValue_Page4(mcontext, 0);
////						b = new Bundle();
////						b.putInt(Settings.Bundle_Non_Settings_Page, 1);
////						b.putInt("BT_TYPE", bttype);
////						((BaseActivity)getActivity()).clearAllFragment();
////						((BaseActivity) getActivity()).changeFragment(new NonParamReturnResultFragment(), b);
////					}
////					else{
////						Measure_SharedPreferences.setValue_Page3(mcontext, 0);
////					}
////					break;
////				case R.id.yes:
////					yes.setSelected(true);
////					no.setSelected(false);
////					if(Page == 3){
////						Measure_SharedPreferences.setValue_Page4(mcontext, 1);
////						b = new Bundle();
////						b.putInt(Settings.Bundle_Non_Settings_Page, 1);
////						b.putInt("BT_TYPE", bttype);
////						((BaseActivity)getActivity()).clearAllFragment();
////						((BaseActivity) getActivity()).changeFragment(new NonParamReturnResultFragment(), b);
////					}
////					else{
////						Measure_SharedPreferences.setValue_Page3(mcontext, 1);
////					}
////					break;
////				default:
////					break;
//				}
//			}
//		}

//		@Override
//		public void setTextSize() {
//			if(MyApplication.getLanguageEnv().equals("en")){
//				switch (Page) {
//				case 0:
//					Measurement_period_text.setTextSize(30);
//					get_up_text.setTextSize(18);
//					fasting_text.setTextSize(15);
//					Postprandial_text.setTextSize(18);
//					sleeping_text.setTextSize(15);
//					break;
//				case 1:
//					eat_status_text.setTextSize(40);
//					Little_text.setTextSize(30);
//					normal_text.setTextSize(24);
//					excess_text.setTextSize(25);
//					break;
//				case 2:
//					yes_text.setTextSize(30);
//					no_text.setTextSize(30);
//					Drinking_text.setTextSize(25);
//					break;
//				case 3:
//					yes_text.setTextSize(30);
//					no_text.setTextSize(30);
//					fever_text.setTextSize(30);
//					break;
//				default:
//					break;
//				}
//			}
//		}

//		@Override
//		protected void LoadData() {
//			// TODO Auto-generated method stub
//
//		}
//	}

	@Override
	public void setTextSize() {

		
	}

	@Override
	protected void LoadData() {
		// TODO Auto-generated method stub
		
	}
}