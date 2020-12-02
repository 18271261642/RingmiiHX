package com.guider.healthring.b31;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import java.util.List;

/**
 * 手动打开权限页面
 * Created by Admin
 * Date 2019/5/7
 */
public class MessageHelpActivity extends WatchBaseActivity implements View.OnClickListener{


    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    //打开通知
    TextView helpNotificationTv;
    //读取联系人的权限
    TextView helpContPermissionTv;
    //拨打电话权限
    TextView helpPhonePermissionTv;
    //读取通话记录权限
    TextView helpCallLogPermissionTv;
    //读取联系人权限
    TextView helpContPermission2Tv;
    //发送短信权限
    TextView helpSMSPermissionTv;


    //联系人
    private boolean contactsFlag = false;
    //电话
    private boolean callPhoneFlag = false;
    //通话记录
    private boolean callLogFlag = false;
    //短信
    private boolean smsFlag = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_help_layout);
        initViewIds();
        initViews();
    }

    private void initViewIds() {
        commentB30BackImg = findViewById(R.id.commentB30BackImg);
        commentB30TitleTv = findViewById(R.id.commentB30TitleTv);
        helpNotificationTv = findViewById(R.id.helpNotificationTv);
        helpContPermissionTv = findViewById(R.id.helpContPermissionTv);
        helpPhonePermissionTv = findViewById(R.id.helpPhonePermissionTv);
        helpCallLogPermissionTv = findViewById(R.id.helpCallLogPermissionTv);
        helpContPermission2Tv = findViewById(R.id.helpContPermission2Tv);
        helpSMSPermissionTv = findViewById(R.id.helpSMSPermissionTv);
        commentB30BackImg.setOnClickListener(this);
        helpNotificationTv.setOnClickListener(this);
        helpContPermissionTv.setOnClickListener(this);
        helpPhonePermissionTv.setOnClickListener(this);
        helpCallLogPermissionTv.setOnClickListener(this);
        helpContPermission2Tv.setOnClickListener(this);
        helpSMSPermissionTv.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //readPermissions();
    }

    private void readPermissions() {
        //判断是否有联系人的权限
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_CONTACTS)){
            helpContPermissionTv.setText(getResources().getString(R.string.string_enable));
            helpContPermission2Tv.setText(getResources().getString(R.string.string_enable));
            contactsFlag = true;
        }else{
            contactsFlag = false;
            helpContPermissionTv.setText(getResources().getString(R.string.string_disable));
            helpContPermission2Tv.setText(getResources().getString(R.string.string_disable));
        }

        //拨打电话
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.CALL_PHONE)){
            callPhoneFlag = true;
            helpPhonePermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            callPhoneFlag = false;
            helpPhonePermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //通话记录
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_CALL_LOG)){
            callLogFlag = true;
            helpCallLogPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else {
            callLogFlag = false;
            helpCallLogPermissionTv.setText(getResources().getString(R.string.string_disable));
        }

        //短信
        if(AndPermission.hasPermissions(MessageHelpActivity.this,Manifest.permission.READ_SMS)){
            smsFlag = true;
            helpSMSPermissionTv.setText(getResources().getString(R.string.string_enable));
        }else{
            smsFlag = false;
            helpSMSPermissionTv.setText(getResources().getString(R.string.string_disable));
        }


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.Messagealert));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.helpNotificationTv:   //通知
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intent, 1001);
                break;
            case R.id.helpContPermission2Tv:    //联系人
//                if(!contactsFlag){
//                    getContactsPermission(Manifest.permission.READ_CONTACTS);
//                }else{
//                    openPermission();
//                }

                break;
            case R.id.helpContPermissionTv: //联系人
                //String contPer = helpContPermissionTv.getText().toString().trim();
//                if(!contactsFlag){
//                    getContactsPermission(Manifest.permission.READ_CONTACTS);
//                }else{
//                    openPermission();
//                }
                break;
            case R.id.helpPhonePermissionTv:    //拨打电话
                //String callPhoneStr = helpPhonePermissionTv.getText().toString().trim();
//                if(!callPhoneFlag){
//                    getContactsPermission(Manifest.permission.CALL_PHONE);
//                }else{
//                    openPermission();
//                }
                break;
            case R.id.helpCallLogPermissionTv:  //通话记录
                //String callLogStr = helpCallLogPermissionTv.getText().toString().trim();
//                if(!callLogFlag){
//                    getContactsPermission(Manifest.permission.READ_CALL_LOG);
//                }else{
//                    openPermission();
//                }

                break;
            case R.id.helpSMSPermissionTv:  //短信
                //String smsStr = helpSMSPermissionTv.getText().toString().trim();
//                if(!smsFlag){
//                    getContactsPermission(Manifest.permission.READ_SMS,Manifest.permission.READ_SMS);
//                }else{
//                    openPermission();
//                }
                break;
        }
    }


    //获取相关权限
    private void getContactsPermission(String...permission){
        AndPermission.with(mContext)
                .runtime()
                .permission(permission)
                .rationale(new Rationale<List<String>>() {
                    @Override
                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                        executor.execute();
                    }

                }).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                //readPermissions();
            }
        }).start();
    }

    /**
     * Set permissions.
     */
    private void openPermission() {

        AndPermission.with(mContext).runtime().setting().start(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {//                Toast.makeText(B30HomeActivity.this,"用户从设置页面返回。",
//                        Toast.LENGTH_SHORT).show();
        }
    }



}
