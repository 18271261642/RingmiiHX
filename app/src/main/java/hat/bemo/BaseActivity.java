package hat.bemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import hat.bemo.APIupload.Controller;
import hat.bemo.location.GPSManager;

import com.guider.healthring.R;

public class BaseActivity extends FragmentActivity {

    public static Context mContext;
    protected static FragmentManager mFragmentManager;

    @SuppressLint("HandlerLeak")
    public static Handler mdHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String mStatus = msg.obj.toString();
            MyApplication.gps_parameters.lms = (LocationManager)
                    MyApplication.context.getSystemService(Context.LOCATION_SERVICE);//取得系統定位服務
            if (mStatus.equals("SOS")) {
                Log.e("GPS", "SOS定位模式");
                GPSManager.bestProvider = GPSManager.lms.getBestProvider(new Criteria(), true);
                Criteria criteria = new Criteria();    //資訊提供者選取標準
                String bestProvider = MyApplication.gps_parameters.lms.getBestProvider(criteria, true);    //選擇精準度最高的提供者
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                MyApplication.gps_parameters.lms.requestLocationUpdates(bestProvider, 60 * 60 * 1000, 0, new GPSManager("false", "false"));
                if (ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyApplication.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                MyApplication.gps_parameters.location = MyApplication.gps_parameters.lms.getLastKnownLocation(bestProvider);
            }
            else if(mStatus.equals("General")){
                Log.e("network", "General定位模式");
                MyApplication.gps_parameters.bestProvider = MyApplication.gps_parameters.lms.
                        getBestProvider(GPSManager.createCoarseCriteria(), true);
                MyApplication.gps_parameters.lms.
                        requestLocationUpdates(MyApplication.gps_parameters.bestProvider, 60*60*1000, 0, new GPSManager("false","false"));
                MyApplication.gps_parameters.location =
                        MyApplication.gps_parameters.lms.getLastKnownLocation(MyApplication.gps_parameters.bestProvider);
            }
            else if(mStatus.equals("Progress")){
                try{
                    Controller.progressDialog.cancel();
                }catch(Exception e){
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mContext = this;
    }

    // 清除Fragment
    public void clearAllFragment() {
        mFragmentManager.popBackStackImmediate();
    }

    // 切換Fragment
    public void changeFragment(Fragment f) {
        changeCenterFragment(f, false, null, null);
    }

    private FragmentTransaction ft;

    @SuppressLint("Recycle")
    private void changeCenterFragment(Fragment f, boolean isAddBack, Bundle b, String tag) {

        try {
            mFragmentManager = getSupportFragmentManager();
            ft = mFragmentManager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            if (b != null) {
                f.setArguments(b);
            }
            if (tag != null && !isAddBack) {
                ft.replace(R.id.content_frame, f, tag);
            } else if(tag != null) {
                ft.replace(R.id.content_frame, f, tag);
                ft.addToBackStack(tag);
            } else {
                ft.replace(R.id.content_frame, f);
                ft.addToBackStack(tag);
            }
            ft.commit();
//			 ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化Fragment(FragmentActivity中呼叫) 不加入返回 加入會為空白
    public void initFragment(Fragment f) {
        changeCenterFragment(f, false, null, "initFragment");
    }

}
