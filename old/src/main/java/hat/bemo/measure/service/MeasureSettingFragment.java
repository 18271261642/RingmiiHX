package hat.bemo.measure.service;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import hat.bemo.BaseFragment;
import com.guider.healthring.R;

public class MeasureSettingFragment extends BaseFragment {
	private View mView;
	private ImageView btn_wt;
	private ImageView btn_bo;
	private ImageView btn_bg;
	private ImageView btn_bp;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.gd800_fragment_settings, container, false);
		bindButton();
		return mView ;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
	}
	
	private void bindButton() {
		btn_wt = (ImageView)mView.findViewById(R.id.btn_item04);
		btn_bp = (ImageView)mView.findViewById(R.id.btn_item01);
		btn_bo = (ImageView)mView.findViewById(R.id.btn_item03);
		btn_bg = (ImageView)mView.findViewById(R.id.btn_item02);	
		
//		btn_bp.setImageResource(R.drawable.item_3);
//		btn_bg.setImageResource(R.drawable.item_2);
//		btn_bo.setImageResource(R.drawable.item_1);
//		btn_wt.setImageResource(R.drawable.item_4);
		
//		MyApplication.setImageSizeSquare(btn_wt, 0.4);
//		MyApplication.setImageSizeSquare(btn_bp, 0.4);
//		MyApplication.setImageSizeSquare(btn_bo, 0.4);
//		MyApplication.setImageSizeSquare(btn_bg, 0.4);
		
//		btn_wt.setOnClickListener(new OnMeasureClickListener());
		btn_bg.setOnClickListener(new OnMeasureClickListener());
		btn_bo.setOnClickListener(new OnMeasureClickListener());
		btn_bp.setOnClickListener(new OnMeasureClickListener());
	}
	
	private class OnMeasureClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {			
			Bundle bundle = new Bundle();
			bundle.putInt(BluetoothBaseActivity.TYPE, v.getId());
			((BluetoothLauncherActivity)getActivity()).changeFragment(new DeviceListFragment(), bundle);	
		}		
	}

	@Override
	public void setTextSize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void LoadData() {
		// TODO Auto-generated method stub
		
	}
}