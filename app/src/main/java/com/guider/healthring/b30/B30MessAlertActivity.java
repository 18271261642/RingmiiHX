package com.guider.healthring.b30;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.guider.healthring.BuildConfig;
import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b31.MessageHelpActivity;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import java.util.ArrayList;
import java.util.List;
import static com.guider.healthring.util.SharedPreferencesUtils.isOpenNotificationPermission;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30消息提醒页面
 */
public class B30MessAlertActivity extends WatchBaseActivity implements View.OnClickListener {

    private static final String TAG = "B30MessAlertActivity";


    ToggleButton b30SkypeTogg;
    ToggleButton b30WhatsAppTogg;
    ToggleButton b30FacebookTogg;
    ToggleButton b30LinkedTogg;
    ToggleButton b30TwitterTogg;
    ToggleButton b30LineTogg;
    ToggleButton b30WechatTogg;
    ToggleButton b30QQTogg;
    ToggleButton b30MessageTogg;
    ToggleButton b30PhoneTogg;

    TextView newSearchTitleTv;

    ToggleButton b30InstagramTogg;
    ToggleButton b30GmailTogg;
    ToggleButton b30SnapchartTogg;
    ToggleButton b30OhterTogg;
    LinearLayout google_gmail;
    View google_gmail_line;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_msgalert);
        initViewIds();

        initViews();

        // 申请权限
        if (!BuildConfig.GOOGLEPLAY)
            requestPermiss();

        getPhoneStatus();
        // 读取社交消息设置
        readSocialMsg();

        // getDoNotDisturb();
    }

    private void initViewIds() {
        b30SkypeTogg = findViewById(R.id.b30SkypeTogg);
        b30WhatsAppTogg = findViewById(R.id.b30WhatsAppTogg);
        b30FacebookTogg = findViewById(R.id.b30FacebookTogg);
        b30LinkedTogg = findViewById(R.id.b30LinkedTogg);
        b30TwitterTogg = findViewById(R.id.b30TwitterTogg);
        b30LineTogg = findViewById(R.id.b30LineTogg);
        b30WechatTogg = findViewById(R.id.b30WechatTogg);
        b30QQTogg = findViewById(R.id.b30QQTogg);
        b30MessageTogg = findViewById(R.id.b30MessageTogg);
        b30PhoneTogg = findViewById(R.id.b30PhoneTogg);
        newSearchTitleTv = findViewById(R.id.newSearchTitleTv);
        b30InstagramTogg = findViewById(R.id.b30InstagramTogg);
        b30GmailTogg = findViewById(R.id.b30GmailTogg);
        b30SnapchartTogg = findViewById(R.id.b30SnapchartTogg);
        b30OhterTogg = findViewById(R.id.b30OhterTogg);
        google_gmail = findViewById(R.id.google_gmail);
        google_gmail_line = findViewById(R.id.google_gmail_line);
        findViewById(R.id.newSearchTitleLeft).setOnClickListener(this);
        findViewById(R.id.newSearchRightImg1).setOnClickListener(this);
        findViewById(R.id.msgOpenNitBtn).setOnClickListener(this);
    }


    private void getPhoneStatus() {
        AudioManager audioManager = (AudioManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ringMode = audioManager.getRingerMode();
            //audioManager.getStreamVolume()
            Log.e(TAG, "-------手环模式=" + ringMode);
            switch (ringMode) {
                case AudioManager.RINGER_MODE_NORMAL:
                    //普通模式
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    //振动模式
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    //静音模式
                    break;
            }

        }
    }


    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            MyApp.getInstance().getApplicationContext().startActivity(intent);
        }

    }


    //申请电话权限
    @SuppressLint("WrongConstant")
    private void requestPermiss() {
        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG)) {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.WRITE_CALL_LOG)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                            executor.execute();
                        }
                    })
                    .start();
        }

        if (!AndPermission.hasPermissions(B30MessAlertActivity.this, Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS)) {
            AndPermission.with(mContext)
                    .runtime()
                    .permission(
                            Manifest.permission.READ_SMS,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG)//,Manifest.permission.WRITE_CALL_LOG)
                    .start();
        }
    }


    /**
     * 跳转到通知使用权
     *
     * @param context
     * @return
     */
    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }


    private void registerPhoneStateListener() {
        CustomPhoneStateListener customPhoneStateListener = new CustomPhoneStateListener(this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void readSocialMsg() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().readSocialMsg(iBleWriteResponse, functionSocailMsgData -> {
                Log.e(TAG, "----读取=" + functionSocailMsgData.toString() + functionSocailMsgData.getPhone() + EFunctionStatus.SUPPORT_OPEN);

                if (functionSocailMsgData.getSkype() == EFunctionStatus.SUPPORT_OPEN) {
                    b30SkypeTogg.setChecked(true);
//                        isSkype = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, true);
                } else {
                    b30SkypeTogg.setChecked(false);
//                        isSkype = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, false);
                }

                if (functionSocailMsgData.getWhats() == EFunctionStatus.SUPPORT_OPEN) {
                    b30WhatsAppTogg.setChecked(true);
//                        isWhats = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, true);
                } else {
                    b30WhatsAppTogg.setChecked(false);
//                        isWhats = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, false);
                }

                if (functionSocailMsgData.getFacebook() == EFunctionStatus.SUPPORT_OPEN) {
                    b30FacebookTogg.setChecked(true);
//                        isFaceBook = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, true);
                } else {
                    b30FacebookTogg.setChecked(false);
//                        isFaceBook = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, false);
                }
                if (functionSocailMsgData.getLinkin() == EFunctionStatus.SUPPORT_OPEN) {
                    b30LinkedTogg.setChecked(true);
//                        isLinkin = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, true);
                } else {
                    b30LinkedTogg.setChecked(false);
//                        isLinkin = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, false);
                }
                if (functionSocailMsgData.getTwitter() == EFunctionStatus.SUPPORT_OPEN) {
                    b30TwitterTogg.setChecked(true);
//                        isTwitter = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, true);
                } else {
                    b30TwitterTogg.setChecked(false);
//                        isTwitter = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, false);
                }
                //viber


                if (functionSocailMsgData.getLine() == EFunctionStatus.SUPPORT_OPEN) {
                    b30LineTogg.setChecked(true);
//                        isLine = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, true);
                } else {
                    b30LineTogg.setChecked(false);
//                        isLine = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, false);
                }
                if (functionSocailMsgData.getWechat() == EFunctionStatus.SUPPORT_OPEN) {
                    b30WechatTogg.setChecked(true);
//                        isWeChat = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, true);
                } else {
                    b30WechatTogg.setChecked(false);
//                        isWeChat = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, false);
                }
                if (functionSocailMsgData.getQq() == EFunctionStatus.SUPPORT_OPEN) {
                    b30QQTogg.setChecked(true);
//                        isQQ = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, true);
                } else {
                    b30QQTogg.setChecked(false);
//                        isQQ = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, false);
                }
                if (functionSocailMsgData.getMsg() == EFunctionStatus.SUPPORT_OPEN) {
                    b30MessageTogg.setChecked(true);
//                        isMsg = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, true);
                } else {
                    b30MessageTogg.setChecked(false);
//                        isMsg = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, false);
                }
                if (functionSocailMsgData.getPhone() == EFunctionStatus.SUPPORT_OPEN) {
                    b30PhoneTogg.setChecked(true);
//                        isOpenPhone = true;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, true);
                } else {
                    b30PhoneTogg.setChecked(false);
//                        isOpenPhone = false;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, false);
                }

                if (functionSocailMsgData.getInstagram() == EFunctionStatus.SUPPORT_OPEN) {
                    b30InstagramTogg.setChecked(true);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, true);
                } else {
                    b30InstagramTogg.setChecked(false);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, false);
                }

                if (functionSocailMsgData.getGmail() == EFunctionStatus.SUPPORT_OPEN) {
                    b30GmailTogg.setChecked(true);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, true);
                } else if (functionSocailMsgData.getGmail() == EFunctionStatus.UNSUPPORT) {
                    google_gmail.setVisibility(View.GONE);
                    google_gmail_line.setVisibility(View.GONE);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, false);
                } else {
                    b30GmailTogg.setChecked(false);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, false);
                }

                if (functionSocailMsgData.getSnapchat() == EFunctionStatus.SUPPORT_OPEN) {
                    b30SnapchartTogg.setChecked(true);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, true);
                } else {
                    b30SnapchartTogg.setChecked(false);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, false);
                }

                if (functionSocailMsgData.getOther() == EFunctionStatus.SUPPORT_OPEN) {
                    b30OhterTogg.setChecked(true);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, true);
                } else {
                    b30OhterTogg.setChecked(false);
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, false);
                }
            });
        }
    }

    private void initViews() {
        newSearchTitleTv.setText(getResources().getString(R.string.string_ocial_message));//社交小心哦
        b30SkypeTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WhatsAppTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30FacebookTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LinkedTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30TwitterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LineTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WechatTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30QQTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30MessageTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30PhoneTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());


        b30InstagramTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30GmailTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30SnapchartTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30OhterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.newSearchRightImg1:
                startActivity(MessageHelpActivity.class);
                break;
            case R.id.msgOpenNitBtn:    //打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 1001);
                break;
        }
    }

    //监听
    private class ToggCheckChanageListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed())
                return;
            switch (buttonView.getId()) {
                case R.id.b30SkypeTogg: //skype
//                    isSkype = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSkype, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30WhatsAppTogg:  //whatspp
//                    isWhats = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWhatsApp, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30FacebookTogg:  //facebook
//                    isFaceBook = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISFacebook, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30LinkedTogg:    //linked
//                    isLinkin = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLinkendln, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30TwitterTogg:   //twitter
//                    isTwitter = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISTwitter, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30LineTogg:  //line
//                    isLine = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISLINE, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30WechatTogg:    //wechat
//                    isWeChat = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISWechart, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30QQTogg:    //qq
//                    isQQ = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISQQ, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30MessageTogg:   //msg
                    //requestPermiss();
//                    isMsg = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISMsm, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30PhoneTogg: //phone
                    //requestPermiss();
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISPhone, isChecked);
//                    isOpenPhone = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISCallPhone, isChecked);
                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);

                    break;
                /**
                 * b30InstagramTogg
                 * b30GmailTogg
                 * b30SnapchartTogg
                 * b30OhterTogg
                 */
                case R.id.b30InstagramTogg:
//                    isb30Instagram = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISInstagram, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30GmailTogg:
//                    isb30Gmail = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISGmail, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30SnapchartTogg:
//                    isb30Snapchart = isChecked;
                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISSnapchart, isChecked);

                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;
                case R.id.b30OhterTogg:
//                    isb30Ohter = isChecked;

                    SharedPreferencesUtils.setParam(B30MessAlertActivity.this, Commont.ISOhter, isChecked);
                    showLoadingDialog("setting...");
                    handler.sendEmptyMessageDelayed(0x88, 200);
                    break;

            }


        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x88:
                    closeLoadingDialog();
                    setSocailMsg();
                    break;
            }
            return false;
        }
    });


    private void setSocailMsg() {
        boolean ISSkype = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISSkype, false);
        boolean ISWhatsApp = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISWhatsApp, false);
        boolean ISFacebook = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISFacebook, false);
        boolean ISLinkendln = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISLinkendln, false);
        boolean ISTwitter = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISTwitter, false);
        boolean ISLINE = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISLINE, false);
        boolean ISWechart = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISWechart, false);
        boolean ISQQ = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISQQ, false);
        boolean ISMsm = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISMsm, false);
        boolean ISPhone = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISPhone, false);
        boolean ISInstagram = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISInstagram, false);
        boolean ISGmail = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISGmail, false);
        boolean ISSnapchart = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISSnapchart, false);
        boolean ISOhter = (boolean) SharedPreferencesUtils.getParam(B30MessAlertActivity.this, Commont.ISOhter, false);
        FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
        //电话提醒
        if (ISPhone) {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
        }
        //短信
        if (ISMsm) {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
        }
        //微信
        if (ISWechart) {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWechat(EFunctionStatus.SUPPORT_CLOSE);
        }
        //QQ
        if (ISQQ) {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setQq(EFunctionStatus.SUPPORT_CLOSE);
        }
        //新浪 不支持
        socailMsgData.setSina(EFunctionStatus.UNSUPPORT);
        //facebook
        if (ISFacebook) {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (ISTwitter) {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setTwitter(EFunctionStatus.SUPPORT_CLOSE);
        }
        //flicker  不支持
        socailMsgData.setFlickr(EFunctionStatus.UNSUPPORT);
        if (ISLinkendln) {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLinkin(EFunctionStatus.SUPPORT_CLOSE);
        }

        if (ISWhatsApp) {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setWhats(EFunctionStatus.SUPPORT_CLOSE);
        }
        if (ISLINE) {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setLine(EFunctionStatus.SUPPORT_CLOSE);
        }

        //instagram
        //socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        //snapchat
        //socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        if (ISSkype) {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSkype(EFunctionStatus.SUPPORT_CLOSE);
        }
        //gmail
        ///socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);

        //instagram
        if (ISInstagram) {
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setInstagram(EFunctionStatus.SUPPORT_CLOSE);
        }
        //Gmail
        if (ISGmail) {
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setGmail(EFunctionStatus.SUPPORT_CLOSE);
        }
        //Snapchart
        if (ISSnapchart) {
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setSnapchat(EFunctionStatus.SUPPORT_CLOSE);
        }
        // 其他
        if (ISOhter) {
            socailMsgData.setOther(EFunctionStatus.SUPPORT_OPEN);
        } else {
            socailMsgData.setOther(EFunctionStatus.SUPPORT_CLOSE);
        }
        Log.e(TAG, "-------------socailMsgData=" + socailMsgData.toString());
        if (socailMsgData.getWechat() != EFunctionStatus.SUPPORT_OPEN &&
                socailMsgData.getLine() != EFunctionStatus.SUPPORT_OPEN) {
            SharedPreferencesUtils.setParam(mContext, isOpenNotificationPermission,
                    false);
        } else SharedPreferencesUtils.setParam(mContext, isOpenNotificationPermission,
                true);
        MyApp.getInstance().getVpOperateManager().settingSocialMsg(iBleWriteResponse,
                functionSocailMsgData -> Log.d(TAG,
                        "-----设置-=" + functionSocailMsgData.toString()), socailMsgData);
    }


    private IBleWriteResponse iBleWriteResponse = i -> {

    };

    public boolean isSupportOpen(EFunctionStatus data, FunctionSocailMsgData functionSocailMsgData) {
        List<EFunctionStatus> list = new ArrayList<>();
        list.add(functionSocailMsgData.getPhone());
        if (list.contains(data) && data == EFunctionStatus.SUPPORT_OPEN) {
            return true;
        } else {
            return false;
        }

    }

}

