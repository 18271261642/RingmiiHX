package com.guider.healthring.b30;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guider.healthring.Commont;
import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.bleutil.MyCommandManager;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.model.datas.HeartData;


/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 手动测量心率
 */
public class ManualMeaureHeartActivity extends WatchBaseActivity implements View.OnClickListener{

    private static final String TAG = "ManualMeaureHeartActivi";

    ImageView commentB30BackImg;
    TextView commentB30TitleTv;
    ImageView commentB30ShareImg;
    ImageView b30cirImg;
    LinearLayout b30ScaleLin;
    TextView b30MeaureHeartValueTv;
    TextView b30finishTv;
    ImageView b30MeaureHeartStartBtn;

    //是否正常测量
    private boolean isMeaure = false;
    //缩放动画
    Animation animationRoate;
    ScaleAnimation animation_suofang;


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    HeartData heartData = (HeartData) msg.obj;
                    if (Commont.isDebug)Log.e(TAG,"----heartData-="+heartData.toString());
                    b30MeaureHeartValueTv.setText(heartData.getData()+"");

                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_meaure_heart);
        initViewIds();
        initViews();
    }

    private void initViewIds(){
        commentB30BackImg =findViewById(R.id.commentB30BackImg);
        commentB30TitleTv =findViewById(R.id.commentB30TitleTv);
        commentB30ShareImg =findViewById(R.id.commentB30ShareImg);
        b30cirImg =findViewById(R.id.b30cirImg);
        b30ScaleLin =findViewById(R.id.b30ScaleLin);
        b30MeaureHeartValueTv =findViewById(R.id.b30MeaureHeartValueTv);
        b30finishTv =findViewById(R.id.b30finishTv);
        b30MeaureHeartStartBtn =findViewById(R.id.b30MeaureHeartStartBtn);
        commentB30BackImg.setOnClickListener(this);
        commentB30ShareImg.setOnClickListener(this);
        b30MeaureHeartStartBtn.setOnClickListener(this);

    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.string_manual_blood_pressure));
        commentB30ShareImg.setVisibility(View.GONE);
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

   @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(ManualMeaureHeartActivity.this);
                break;
            case R.id.b30MeaureHeartStartBtn:   //开始和结束
                if(MyCommandManager.DEVICENAME != null){
                    if(!isMeaure){
                        isMeaure = true;
                        b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_pause);
                        startAllAnimat(b30ScaleLin,b30cirImg);
                        MyApp.getInstance().getVpOperateManager().startDetectHeart(iBleWriteResponse, new IHeartDataListener() {
                            @Override
                            public void onDataChange(HeartData heartData) {
                                if(heartData != null){
                                    Message message = handler.obtainMessage();
                                    message.obj = heartData;
                                    message.what = 1001;
                                    handler.sendMessage(message);
                                }


                            }
                        });

                    }else{
                        b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
                        isMeaure = false;
                        stopAllAnimat(b30ScaleLin,b30cirImg);
                        b30finishTv.setText(getResources().getString(R.string.string_test_come));//测量完毕
                        MyApp.getInstance().getVpOperateManager().stopDetectHeart(iBleWriteResponse);
                    }
                }else{
                    b30finishTv.setText(getResources().getString(R.string.disconnted));
                }
                break;
        }
    }


    private void startAllAnimat(View view1,View view2){
        startFlick(view1);  //开启缩放动画
        startAnimat(view2); //开启旋转动画

    }

    //停止所有动画
    private void stopAllAnimat(View view1,View view2){
        stopScanlAni(view1);
        stopRoateAnimt(view2);
    }

    private void stopScanlAni(View view){
        if(view != null){
            view.clearAnimation();
        }
    }


    private void stopRoateAnimt(View view){
        if(view != null){
            view.clearAnimation();
        }

    }


    //缩放动画
    public static void startFlick(View view) {
        if (null == view) {
            return;
        }
        ScaleAnimation animation_suofang = new ScaleAnimation(1.4f, 1.0f,
                1.4f, 1.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation_suofang.setDuration(3000);                     //执行时间
        animation_suofang.setRepeatCount(-1);                   //重复执行动画
        animation_suofang.setRepeatMode(Animation.REVERSE);     //重复 缩小和放大效果
        view.startAnimation(animation_suofang);

    }

    //旋转动画
    private void startAnimat(View view) {
        if(view == null){
            return;
        }
        animationRoate = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        animationRoate.setInterpolator(lin);

        animationRoate.setDuration(3 * 1000);
        animationRoate.setRepeatCount(-1);//动画的反复次数
        animationRoate.setFillAfter(true);//设置为true，动画转化结束后被应用
        view.startAnimation(animationRoate);//開始动画

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (b30MeaureHeartStartBtn!=null)b30MeaureHeartStartBtn.setImageResource(R.drawable.detect_heart_start);
        isMeaure = false;
        stopAllAnimat(b30ScaleLin,b30cirImg);
        b30finishTv.setText(getResources().getString(R.string.string_test_come));//"测量完毕"
        MyApp.getInstance().getVpOperateManager().stopDetectHeart(iBleWriteResponse);
    }
}
