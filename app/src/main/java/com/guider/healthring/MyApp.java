package com.guider.healthring;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.common.core.MyUtils;
import com.guider.healthring.B18I.b18idb.DBManager;
import com.guider.healthring.activity.wylactivity.wyl_util.service.AlertService;
import com.guider.healthring.activity.wylactivity.wyl_util.service.NewSmsBroadCastReceiver;
import com.guider.healthring.b30.service.B30DataServer;
import com.guider.healthring.b30.service.NewB30ConnStateService;
import com.guider.healthring.bzlmaps.PhoneSosOrDisPhone;
import com.guider.healthring.siswatch.utils.CustomPhoneStateListener;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.guider.libbase.map.IMapLocation;
import com.mob.MobSDK;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.tencent.bugly.Bugly;
import com.veepoo.protocol.VPOperateManager;
import org.litepal.LitePalApplication;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by thinkpad on 2016/7/20.
 */

public class MyApp extends LitePalApplication {

    public static boolean isLogin = false;
    private static MyApp application;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //用于退出activity
    private List<AppCompatActivity> activities;

    public static Context context;
    /**
     * 手环MAC地址
     */
    private String macAddress;
    /**
     * 是否正在上传数据
     */
    private boolean uploadDate;
    private boolean uploadDateGD = false;
    private boolean uploadDateGDNew = false;
    private boolean uploadDateGDHrv = false;


    public static PhoneSosOrDisPhone phoneSosOrDisPhone;

    private IMapLocation mIMapLocation;
    static {
        phoneSosOrDisPhone = new PhoneSosOrDisPhone();
    }

    public MyApp() {
        application = this;
    }

    public static MyApp getInstance() {
        return application;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    static RequestQueue requestQueue;
    //B18I数据库管理
    private static DBManager dbManager;

    public static boolean isOne = true;
    public static boolean AppisOne = false;
    public static boolean AppisOneStar = false;


    //监听来电
    private static CustomPhoneStateListener customPhoneStateListener;

    //B30手环
    private VPOperateManager vpOperateManager;

    //B30手环的服务
    private static NewB30ConnStateService b30ConnStateService;

    // public LocationService locationService;

    NewSmsBroadCastReceiver newSmsBroadCastReceiver;

    @Override
    public void onCreate() {
        // 重新处理accountId
        try {
            long strAccountId = (long) SharedPreferencesUtils.getParam(getApplicationContext(), "accountIdGD", 0L);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            int strAccountId = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "accountIdGD", 0);
            if (strAccountId != 0)
                SharedPreferencesUtils.setParam(getApplicationContext(), "accountIdGD", (long)strAccountId);
        }

        MyUtils.setMacAddress(BuildConfig.MAC); // 模拟手环APP
        ApiUtil.init(getApplication() , MyUtils.getMacAddress());
        Commont.GAI_DE_BASE_URL = BuildConfig.APIHDURL + "api/v1/";
        MyUtils.application = getApplication();
        super.onCreate();
        AppisOne = true;
        AppisOneStar = true;
        // LeakCanary.install(this);
        application = this;
        context = this;
        activities = new ArrayList<>();

        // locationService = new LocationService(application);

        Bugly.init(this, "ff6d0ec595", true);

        MobSDK.init(this, "2dc65dc4724aa", "5c4cd9ab545da0ccebd0d4b6d46c73fd");

        // 启动B30的服务
        startB30Server();
        bindAlertServices();    //绑定通知的服务

        try {
            setDatabase();
            dbManager = DBManager.getInstance(context);
        } catch (Exception error) {
            error.printStackTrace();
        }

        // 如果是国内版本，注册短信及电话接收，以便手环能够收到提醒
        if (!BuildConfig.GOOGLEPLAY) {
            // 短信
            newSmsBroadCastReceiver = new NewSmsBroadCastReceiver();
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(newSmsBroadCastReceiver, intentFilter);
            // 电话
            TelephonyManager manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            manager.listen(getCustomPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }

        // 打印签名信息
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.guider.healthringx",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("SHA", new BigInteger(1, md.digest()).toString(16));
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }



    public static B30DataServer getB30DataServer() {
        return B30DataServer.getB30DataServer();
    }

    //B30的服务
    public  NewB30ConnStateService getB30ConnStateService() {
        if (b30ConnStateService == null) {
            startB30Server();
        }
        return b30ConnStateService;
    }

    //启动
    public static void startB30Server() {
        Intent ints = new Intent(application.getApplicationContext(), NewB30ConnStateService.class);
        application.bindService(ints, b30ServerConn, BIND_AUTO_CREATE);
    }

    //B30
    private static ServiceConnection b30ServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof NewB30ConnStateService.B30LoadBuilder) {
                b30ConnStateService = ((NewB30ConnStateService.B30LoadBuilder) service).getB30Service();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            b30ConnStateService = null;
        }
    };

    public VPOperateManager getVpOperateManager() {

        if (vpOperateManager == null) {
            vpOperateManager = VPOperateManager.getMangerInstance(application);

        }
        return vpOperateManager;
    }




    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();

        // mIMapLocation.stop();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }


    static W30SBLEManage mW30SBLEManage;

    public static W30SBLEManage getmW30SBLEManage() {
        if (mW30SBLEManage == null) {
            return W30SBLEManage.getInstance(context);
        } else {
            return mW30SBLEManage;
        }
    }

    public static void setmW30SBLEManage(W30SBLEManage mW30SBLEManage) {
        MyApp.mW30SBLEManage = mW30SBLEManage;
    }

    /**
     * 定时读取手环数据
     */
    public static final String RefreshBroad = "com.example.bozhilun.android.RefreshBroad";


    //监听来电
    public static CustomPhoneStateListener getCustomPhoneStateListener() {
        if (customPhoneStateListener == null) {
            synchronized (CustomPhoneStateListener.class) {
                if (customPhoneStateListener == null) {
                    customPhoneStateListener = new CustomPhoneStateListener(application);
                }
            }
        }
        return customPhoneStateListener;
    }


    private void bindAlertServices() {
        Intent ints = new Intent(application.getApplicationContext(), AlertService.class);
        bindService(ints, alertConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection alertConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MyApp", "-----conn---");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MyApp", "-----disconn---");
        }
    };


    public static MyApp getApplication() {
        return application;
    }


    /**
     * 设置greenDao
     */

    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "guiderhx-notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }

    public static DBManager getDBManager() {
        return dbManager;
    }


    /**
     * 添加Activity
     */
    public void addActivity(AppCompatActivity activity) {
        // 判断当前集合中不存在该Activity
        if (!activities.contains(activity)) {
            activities.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (AppCompatActivity activity : activities) {
            //unregisterReceiver(refreshBroadcastReceiver);
            activity.finish();
        }
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        return requestQueue;
    }

    /**
     * 全局手环MAC地址
     */
    public String getMacAddress() {
        if (macAddress == null)
            macAddress = (String) SharedPreferencesUtils.readObject(this, Commont.BLEMAC);
        return macAddress;
    }

    /**
     * 全局手环MAC地址
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * 是否正在上传数据
     */
    public boolean isUploadDate() {
        return uploadDate;
    }

    /**
     * 是否正在上传数据
     */
    public void setUploadDate(boolean uploadDate) {
        this.uploadDate = uploadDate;
    }


    public boolean isUploadDateGD() {
        return uploadDateGD;
    }


    public void setUploadDateGD(boolean uploadDateGD) {
        this.uploadDateGD = uploadDateGD;
    }

    public boolean isUploadDateGDHrv() {
        return uploadDateGDHrv;
    }


    public void setUploadDateGDHrv(boolean uploadDateGDHrv) {
        this.uploadDateGDHrv = uploadDateGDHrv;
    }

    public boolean isUploadDateGDNew() {
        return uploadDateGDNew;
    }

    public void setUploadDateGDNew(boolean uploadDateGDNew) {
        this.uploadDateGDNew = uploadDateGDNew;
    }
}
