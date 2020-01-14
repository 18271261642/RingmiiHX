package hat.bemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.guider.healthring.R;

import java.util.ArrayList;
import java.util.List;

import hat.bemo.setting.SharedPreferences_status;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    private String IMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAppData();

        permission1();

        if (IMEI == null || IMEI.equals("")) {
            IMEI = SharedPreferences_status.Get_IMEI(MainActivity.this);
        }
        System.out.println("Mark IMEI = " + IMEI);

        Log.e("MainActivity", "IMEI" + IMEI);

        String[] permissions = new String[]{
                ACCESS_COARSE_LOCATION,
                ACCESS_WIFI_STATE,
                CALL_PHONE,
                CHANGE_WIFI_STATE,
                WRITE_EXTERNAL_STORAGE
        };

        List<String> mPermissionList = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            openservice();
//            Toast.makeText(MainActivity.this,"已经授权",Toast.LENGTH_LONG).show();
        } else {//请求权限方法
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        }


        this.finish();


    }

    private void permission1() {


        // The request code used in ActivityCompat.requestPermissions()
// and returned in the Activity's onRequestPermissionsResult()
//        int PERMISSION_ALL = 1;
//        String[] PERMISSIONS = {ACCESS_WIFI_STATE, CHANGE_WIFI_STATE, ACCESS_COARSE_LOCATION, WRITE_EXTERNAL_STORAGE, CALL_PHONE};
//
//        if(!hasPermissions(this, PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//        }

//        int permission = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // 無權限，向使用者請求
//            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
////            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
////
//        }else{
//            //已有權限，執行儲存程式
//            permission2();
//        }

    }

    private void permission2() {

        int permission = ActivityCompat.checkSelfPermission(this, ACCESS_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_WIFI_STATE}, 2);
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//
        } else {
            //已有權限，執行儲存程式
            permission3();
        }

    }

    private void permission3() {

        int permission = ActivityCompat.checkSelfPermission(this, CHANGE_WIFI_STATE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this, new String[]{CHANGE_WIFI_STATE}, 3);
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//
        } else {
            //已有權限，執行儲存程式
            permission4();
        }

    }

    private void permission4() {

        int permission = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 4);
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//
        } else {
            //已有權限，執行儲存程式
            permission5();
        }

    }

    private void permission5() {

        int permission = ActivityCompat.checkSelfPermission(this, CALL_PHONE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, 5);
//            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
//
        } else {
            //已有權限，執行儲存程式
            openservice();
        }

    }

    private void openservice() {

        Intent service = new Intent();
        service.setClass(this, TopFloatService.class);
        //启动服务
        startService(service);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        System.out.println("Mark widths = " + metrics.widthPixels + ", height = " + metrics.heightPixels);
        SharedPreferences_status.SaveWidth(this, metrics.widthPixels);
        SharedPreferences_status.SaveHeight(this, metrics.heightPixels);
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        if(requestCode == 1) {
//            System.out.println("Mark 開了");
//            permission2();
//        }
//        else if(requestCode == 2){
//            permission3();
//        }
//        else if(requestCode == 3){
//            openservice();
//        }
//
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限已申请
                openservice();
            } else {
                //权限已拒绝

            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        //权限未申请
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getAppData() {

//        Intent intent = getIntent();
//        if (intent.getFlags() == 101) {
//            String data_str = intent.getStringExtra("data");
//            if (data_str != null && !data_str.equals("")) {
//                IMEI = data_str;
//            }
//            //tag: 调用者传递过来的数据
//        }

        Intent intent = getIntent();
        if (intent.getFlags() == 101) {
            String data_str = intent.getStringExtra("data");
            if (data_str != null && !data_str.equals("")) {
                IMEI = data_str;
                Log.e("=main==IMEI ",data_str);
                SharedPreferences_status.save_IMEI(MyApplication.context,IMEI);
                //ToastUtil.showShort(MainActivity.this,"接收到的mac"+MyCommandManager.ADDRESS);
            }
            //tag: 调用者传递过来的数据
        }



    }


}
