package hat.bemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.guider.healthring.MyApp;
import com.guider.healthring.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import hat.bemo.setting.SharedPreferences_status;

public class TopFloatService extends Service implements View.OnClickListener, View.OnKeyListener {
    WindowManager wm = null;
    WindowManager.LayoutParams ballWmParams = null;
    WindowManager wm2 = null;
    WindowManager.LayoutParams ballWmParams2 = null;
    private View ballView, markView, LoginView;
    private View menuView;
    private float mTouchStartX, mdownx, mupx, nowx;
    private float mTouchStartY, mdowny, mupy, nowy;
    private float x;
    private float y;
    private int markout = 0, marklong = 0, mlongtime = 0;
    private RelativeLayout menuLayout, mrl;
    private Button floatImage, bt1, bt2, bt3, bt4, loginbt;
    private PopupWindow pop;
    private RelativeLayout menuTop;
    private boolean ismoving = false;
    private FrameLayout mfl;
    private Handler Markhandler, Markhandler2;
    private int SOSCount = 0;


    private UpdateAlarmLoc alarm;

    //监听Home
    private HomeWatcherReceiver mHomeKeyReceiver;
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "globalactions";
    public static final String SYSTEM_DIALOG_REASON_BACK_KEY = "backkey";
    public static final String SYSTEM_DIALOG_REASON_Lock_KEY = "lock";


    @Override
    public void onCreate() {
        super.onCreate();
//        //加载辅助球布局
        ballView = LayoutInflater.from(this).inflate(R.layout.floatball, null);
        floatImage = (Button) ballView.findViewById(R.id.float_image);
        mfl = (FrameLayout) ballView.findViewById(R.id.fl);

        markView = LayoutInflater.from(this).inflate(R.layout.markmenu, null);
        bt1 = (Button) markView.findViewById(R.id.mbt1);
        mrl = (RelativeLayout) markView.findViewById(R.id.mrl);
        loginbt = (Button) markView.findViewById(R.id.Loginbt);
        bt2 = (Button) markView.findViewById(R.id.mbt2);

        if (mHomeKeyReceiver != null) {
            unregisterReceiver(mHomeKeyReceiver);
            mHomeKeyReceiver = null;
        }
        //Mark 監聽Home
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        try {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(MyApp.getContext());
            /**
             * 判断广播没注册的时候注册
             */
            boolean isRegister = isRegister(manager, "android.intent.action.CLOSE_SYSTEM_DIALOGS");
            if (!isRegister) {
                manager.registerReceiver(mHomeKeyReceiver, homeFilter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

//        bt1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Toast.makeText(getApplicationContext(), "藍牙", Toast.LENGTH_SHORT).show();
//                ComponentName com = new ComponentName("hat.bemo", "hat.bemo.ToBT");
//                Intent intent = new Intent();
//                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent.setComponent(com));
//
//
//            }
//        });
//
//        bt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ComponentName com = new ComponentName("hat.bemo", "hat.bemo.ToCall");
//                Intent intent = new Intent();
//                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent.setComponent(com));
//
//            }
//        });
//
//        loginbt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                ShowLoginView();
//                Toast.makeText(getApplicationContext(), "登入", Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        mrl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                backball();
//
//            }
//        });

        setUpFloatMenuView();
        //createView();

    }


    /**
     *
     * action IntentFilter中的某一个action，因为获取到的是IntentFilter的所有action，所以只要匹配一个就可以
     *
     */
    private boolean isRegister(LocalBroadcastManager manager, String action) {
        boolean isRegister = false;
        try {
            Field mReceiversField = manager.getClass().getDeclaredField("mReceivers");
            mReceiversField.setAccessible(true);
//            String name = mReceiversField.getName();
            HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = (HashMap<BroadcastReceiver, ArrayList<IntentFilter>>) mReceiversField.get(manager);

            for (BroadcastReceiver key : mReceivers.keySet()) {
                ArrayList<IntentFilter> intentFilters = mReceivers.get(key);
                Log.e("Key: ", key + " Value: " + intentFilters);
                for (int i = 0; i < intentFilters.size(); i++) {
                    IntentFilter intentFilter = intentFilters.get(i);
                    Field mActionsField = intentFilter.getClass().getDeclaredField("mActions");
                    mActionsField.setAccessible(true);
                    ArrayList<String> mActions = (ArrayList<String>) mActionsField.get(intentFilter);
                    for (int j = 0; j < mActions.size(); j++) {
                        if (mActions.get(i).equals(action)) {
                            isRegister = true;
                            break;
                        }
                    }
                }
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return isRegister;
    }

    private void ShowLoginView() {

        ComponentName com = new ComponentName("hat.bemo", "hat.bemo.Login");
        Intent intent = new Intent();
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent.setComponent(com));

//        LoginView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loginview, null);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("登入");
//        builder.setView(LoginView);
//        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.create().show();

    }

    /**
     * 窗口菜单初始化
     */
    private void setUpFloatMenuView() {
        menuView = LayoutInflater.from(this).inflate(R.layout.floatmenu, null);
        menuLayout = (RelativeLayout) menuView.findViewById(R.id.menu);
        menuTop = (RelativeLayout) menuView.findViewById(R.id.lay_main);
        menuLayout.setOnClickListener(this);
        menuLayout.setOnKeyListener(this);
        menuTop.setOnClickListener(this);
    }

    /**
     * 通过MyApplication创建view，并初始化显示参数
     */
    @SuppressLint("ClickableViewAccessibility")
    private void createView() {
        wm = (WindowManager) getApplicationContext().getSystemService("window");
        ballWmParams = ((MyApplication) getApplication()).getMywmParams();
        ballWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        //权限判断
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(!Settings.canDrawOverlays(getApplicationContext())) {
//                //启动Activity让用户授权
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                startActivity(intent);
//                return;
//            } else {
//                //执行6.0以上绘制代码
//                ballWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//            }
//        } else {
//            //执行6.0以下绘制代码
//            ballWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }

        ballWmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        ballWmParams.gravity = Gravity.LEFT | Gravity.TOP;
        ballWmParams.x = 0;
        ballWmParams.y = 0;
        ballWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ballWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ballWmParams.format = PixelFormat.RGBA_8888;
        //添加显示层
        wm.addView(ballView, ballWmParams);
        //注册触碰事件监听器
        floatImage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ismoving = false;
                        mTouchStartX = (int) event.getX();
                        mTouchStartY = (int) event.getY();
                        mdownx = (int) event.getX();
                        mdowny = (int) event.getY();

                        markout = 0;
                        marklong = 0;
                        mlongtime = 0;
                        mLongClick();

//                        System.out.println("Mark 按下 x = " + mdownx + ", y = " + mdowny + ",x2 = "
//                        + x + ",y2 = " + y);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        ismoving = true;

                        nowx = (int) event.getX();
                        nowy = (int) event.getY();

                        updateViewPosition();
//                        System.out.println("mark touch move = " + markout);


                        break;
                    case MotionEvent.ACTION_UP:
                        mTouchStartX = (int) event.getX();
                        mTouchStartY = (int) event.getY();

                        mupx = (int) event.getX();
                        mupy = (int) event.getX();

                        Markhandler.removeCallbacks(LongClick);
                        mlongtime = 0;

//                        System.out.println("Mark 拿開 x = " + mTouchStartX + ", y = " + mTouchStartY);

                        if (markout == 0) {

//                            menuview();

//                            ComponentName com = new ComponentName("hat.bemo", "hat.bemo.ToCall");
//                            Intent intent = new Intent();
//                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent.setComponent(com));

                            //ComponentName com = new ComponentName("hat.bemo", "hat.bemo.ToCall");
                            Intent intent = new Intent(MyApplication.context,ToCall.class);
                            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent.setComponent(com));
                            startActivity(intent);

                        } else {

                            markout = 0;
                            ToTheWall();
                        }
                        break;
                }
                //如果拖动则返回false，否则返回true
                if (!ismoving ) {
                    return false;
                } else {
                    return true;
                }
            }

        });


//        floatImage.setOnLongClickListener(new View.OnLongClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//            @Override
//            public boolean onLongClick(View v) {
//                if(marklong == 0) {
//                    markout = 1;
//                    alarm();
//                }
//                return false;
//            }
//        });


        //注册点击事件监听器
//        floatImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                System.out.println("Mark 有點到嗎？");
////                ComponentName com = new ComponentName("hat.bemo", "hat.bemo.Login");
////                Intent intent = new Intent();
////                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent.setComponent(com));
//
////               bt1.setVisibility(View.VISIBLE);
//                menuview();
//
////                wm.removeView(ballView);
////                DisplayMetrics dm = getResources().getDisplayMetrics();
////                pop = new PopupWindow(menuView, dm.widthPixels, dm.heightPixels);
////                pop.showAtLocation(ballView, Gravity.CENTER, 0, 0);
////                pop.update();
//            }
//        });
    }

    public void mLongClick() {
        marklong = 1;
        Markhandler = new Handler();
        Markhandler.postDelayed(LongClick, 1000);
    }

    Runnable LongClick = new Runnable() {
        // yooooo
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void run() {

            Markhandler.removeCallbacks(LongClick);

            if (mlongtime == 1) {
                alarm();
            } else {
                mlongtime++;
                Markhandler = new Handler();
                Markhandler.postDelayed(LongClick, 1000);
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void alarm() {

        markout = 2;
//        Toast.makeText(getApplicationContext(), "定位", Toast.LENGTH_SHORT).show();
        System.out.println("Mark 長按囉");
        alarm = new UpdateAlarmLoc(this);

        Intent intentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "0961540729"));
        intentDial.setFlags(intentDial.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startActivity(intentDial);

    }

//    private void menuview(){
//        Markhandler.removeCallbacks(LongClick);
//
//        wm2 = (WindowManager) getApplicationContext().getSystemService("window");
//        ballWmParams2 =  ((MyApplication) getApplication()).getMywmParams();
//        ballWmParams2.type = WindowManager.LayoutParams.TYPE_PHONE;
//        ballWmParams2.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        ballWmParams2.gravity = Gravity.CENTER;
//        ballWmParams2.x = 0;
//        ballWmParams2.y = 0;
//        ballWmParams2.width = SharedPreferences_status.GetWidth(this);
//        ballWmParams2.height = SharedPreferences_status.GetHeight(this);
//        ballWmParams2.format = PixelFormat.RGBA_8888;
//        //添加显示层
//        ballView.setVisibility(View.INVISIBLE);
//        wm2.addView(markView, ballWmParams2);
//
//    }

    private void backball() {

        ballWmParams.gravity = Gravity.LEFT | Gravity.TOP;
        ballWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        ballWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wm2.removeView(markView);

        ballView.setVisibility(View.VISIBLE);
    }

    private void ToTheWall() {

        int mx, my, cx, cy, px, py, hx, hy;
        mx = ballWmParams.x;
        my = ballWmParams.y;
        //手機大小
        px = SharedPreferences_status.GetWidth(this);
        py = SharedPreferences_status.GetHeight(this);
        //靠右多少
        cx = px - mx;
        //靠下多少
        cy = py - my;
        //手機一半大小
        hx = px / 2;
        hy = py / 2;
        System.out.println("Mark mx = " + mx + ", my = " + my + ", px = " + px + ", py = " + py
                + ", cx = " + cx + ", cy = " + cy);

        //右邊
        if (mx > hx) {

            //右下
            if (my > hy) {

                //往下靠
                if (cx > cy) {

                    ballWmParams.x = (int) (x - mTouchStartX);
                    ballWmParams.y = py;

                }
                //往右靠
                else {
                    ballWmParams.x = px;
                    ballWmParams.y = (int) (y - mTouchStartY);
                }

            }
            //右上
            else {

                //往上靠
                if (cx > my) {

                    ballWmParams.x = (int) (x - mTouchStartX);
                    ballWmParams.y = 0;

                }

                //往右靠
                else {

                    ballWmParams.x = px;
                    ballWmParams.y = (int) (y - mTouchStartY);

                }

            }

        }
        //左邊
        else {

            //左下
            if (my > hy) {

                //往下靠
                if (mx > cy) {

                    ballWmParams.x = (int) (x - mTouchStartX);
                    ballWmParams.y = py;

                }

                //往左靠
                else {
                    ballWmParams.x = 0;
                    ballWmParams.y = (int) (y - mTouchStartY);
                }

            }

            //左上
            else {

                //往上靠
                if (mx > my) {
                    ballWmParams.x = (int) (x - mTouchStartX);
                    ballWmParams.y = 0;
                }

                //往左靠
                else {
                    ballWmParams.x = 0;
                    ballWmParams.y = (int) (y - mTouchStartY);
                }

            }

        }

        wm.updateViewLayout(ballView, ballWmParams);


        mTouchStartX = 0;
        mTouchStartY = 0;
    }

    /**
     * 更新view的显示位置
     */
    private void updateViewPosition() {

        float dx, dy;
        dx = 0;
        dy = 0;
        float mx, my;
        mx = nowx;
        my = nowy;

        if (mx > mdownx) {
            dx = mx - mdownx;
            if (my > mdowny) {
                dy = my - mdowny;
            } else {
                dy = mdowny - my;
            }
        } else {
            dx = mdownx - mx;
            if (my > mdowny) {
                dy = my - mdowny;
            } else {
                dy = mdowny - my;
            }
        }

        if (dx + dy > 200) {

            markout = 1;
            mlongtime = 0;
            Markhandler.removeCallbacks(LongClick);


        }

        if (markout == 1) {
            ballWmParams.x = (int) (x - mTouchStartX);
            ballWmParams.y = (int) (y - mTouchStartY);

            wm.updateViewLayout(ballView, ballWmParams);

            dx = 0;
            dy = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Mark 監聽Home
    class HomeWatcherReceiver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action == null)
                return;
//            System.out.println("Mark action = " + action);

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

//                System.out.println("Mark What = " + reason);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    System.out.println("Mark Home");

                    if (SOSCount == 0) {

                        SOSCount = 1;

                        Markhandler2 = new Handler();
                        Markhandler2.postDelayed(SOSStop, 5000);

                    } else {

                        SOSCount = 0;
                        System.out.println("Mark SOS!!!");
                        Markhandler2.removeCallbacks(SOSStop);
                        alarm();

                    }

                }

            }
        }

    }

    Runnable SOSStop = new Runnable() {
        // yooooo
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void run() {

            System.out.println("Mark 5秒囉!!");
            Markhandler2.removeCallbacks(SOSStop);
            SOSCount = 0;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_main:
                Toast.makeText(getApplicationContext(), "111", Toast.LENGTH_SHORT).show();
                backball();
                break;

            default:
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                }
                break;
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        System.out.println("mark KeyCode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                System.out.println("mark HOME");
                break;

            case KeyEvent.KEYCODE_BACK:
                System.out.println("mark HOME");
                break;

            default:
                break;
        }
        return true;
    }


}