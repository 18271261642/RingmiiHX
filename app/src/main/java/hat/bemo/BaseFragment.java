package hat.bemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment{

	protected View mView;
	protected Context mContext;
	protected FrameLayout content_title_frame;
	protected TextView tv_title;
	protected static Context Context;
	public abstract void setTextSize();
	protected abstract void LoadData();
	private BluetoothAdapter mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();
	
	public void setTextView(TextView tv_title) {
		this.tv_title = tv_title;
	}
	
	public void setContent_title_text(TextView tv_title, int title, int drawable) {
		tv_title.setText(title);
		Drawable textDrawable = getResources().getDrawable(drawable);
		textDrawable.setBounds(0, 0, 20, 20);
		tv_title.setCompoundDrawablesWithIntrinsicBounds(textDrawable, null,
				null, null);
	}
	
	public interface BluetoothServiceListener{
		public void disconnected();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = getActivity();
		Context = getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LoadData();
	}
	
	public void closeFragment(boolean closeAll) {
		if(closeAll){
		FragmentManager fm = getActivity().getSupportFragmentManager();
		for(int entry = 0; entry < fm.getBackStackEntryCount(); entry++){
			fm.popBackStack();
			}
		}
		else
		  getActivity().getSupportFragmentManager().popBackStack();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		setTextSize();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}