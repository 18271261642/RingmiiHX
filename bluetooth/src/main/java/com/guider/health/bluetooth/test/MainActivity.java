//package com.guider.health.bluetooth.test;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.guider.health.bluetooth.R;
//import com.guider.health.bluetooth.core.Params;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.guider.health.bluetooth.core.Params.MSG_DATA;
//
//
///**
// * MainActivity
// */
//public class MainActivity extends AppCompatActivity implements Toastinerface {
//
//    final String TAG = "MainActivity";
//
//    TabLayout tabLayout;
//    ViewPager viewPager;
//    MyPagerAdapter pagerAdapter;
//    String[] titleList=new String[]{"选择设备","数据展示"};
//    List<Fragment> fragmentList=new ArrayList<>();
//
//    DeviceListFragment deviceListFragment;
//    DataShowFragment dataTransFragment;
//
//    BluetoothAdapter bluetoothAdapter;
//
//    Handler uiHandler =new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what){
//                case Params.MSG_CONNECT_TO_SERVER:
//                    BluetoothDevice serverDevice = (BluetoothDevice) msg.obj;
//                    dataTransFragment.connectServer(serverDevice);
//                    viewPager.setCurrentItem(1);
//                    break;
//                case Params.MSG_CLIENT_REV_NEW:
//                    System.out.println("收到数据--------");
//                    String str =  msg.obj.toString();
//                    dataTransFragment.updateDataView(str, Params.REMOTE);
//                    break;
//                case Params.CONNECT_FAILE:
//                    Log.e(TAG,"--------- connect faile");
//                    toast("连接失败");
//                    break;
//                case MSG_DATA:
//                    Log.e(TAG,"--------- connect faile");
//                    Toast.makeText(MainActivity.this,msg.getData().getString("data"),Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
//
//        initUI();
//    }
//
//
//    /**
//     * 返回 uiHandler
//     * @return
//     */
//    public Handler getUiHandler(){
//        return uiHandler;
//    }
//
//    /**
//     * 初始化界面
//     */
//    private void initUI() {
//        tabLayout= findViewById(R.id.tab_layout);
//        viewPager= findViewById(R.id.view_pager);
//
//        tabLayout.addTab(tabLayout.newTab().setText(titleList[0]));
//        tabLayout.addTab(tabLayout.newTab().setText(titleList[1]));
//
//        deviceListFragment=new DeviceListFragment();
//        dataTransFragment=new DataShowFragment();
//        deviceListFragment.setToastinerface(this);
//        fragmentList.add(deviceListFragment);
//        fragmentList.add(dataTransFragment);
//
//        pagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(pagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
//    }
//
//    @Override
//    public void toast(String str) {
//        Message message = new Message();
//        Bundle bundle=new Bundle();
//        bundle.putString("data", str);
//        message.setData(bundle);
//        message.what = MSG_DATA;
//        uiHandler.sendMessage(message);
//    }
//
//
//    public class MyPagerAdapter extends FragmentPagerAdapter{
//
//        public MyPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragmentList.size();
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titleList[position];
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        uiHandler.removeCallbacksAndMessages(null);
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
//    }
//}
