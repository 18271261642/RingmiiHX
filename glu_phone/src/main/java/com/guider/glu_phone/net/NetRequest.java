package com.guider.glu_phone.net;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.guider.glu.model.BodyIndex;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.IUserHDApi;
import com.guider.health.apilib.model.SimpleUserInfo;
import com.guider.health.apilib.model.UserInfo;
import com.guider.health.apilib.model.hd.Healthrange;
import com.guider.health.apilib.model.hd.NonbsBean;
import com.guider.health.common.core.HealthRange;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.net.NetStateController;
import com.guider.health.common.net.net.RetrofitLogInterceptor;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by haix on 2019/8/11.
 */

public class NetRequest {
    /*
    private static final int TIME_OUT = 60;
    private OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new RetrofitLogInterceptor())
            .build();
    */
    public interface NetCallBack{
        void result(int code, String result);
    }

    private Retrofit retrofit;
    private static NetRequest netRequest = new NetRequest();
    private NetRequest(){
    }

    public static NetRequest getInstance(){
        return netRequest;
    }


    public void getUserInfo(WeakReference<Activity> activity, final NetCallBack callBack) {
        if (activity.get() != null) {
            if (!NetStateController.isNetworkConnected(activity.get())) {
                Toast.makeText(activity.get(), activity.get().getResources().getString(com.guider.glu.R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ApiUtil.createApi(IGuiderApi.class , false).getUserInfo(UserManager.getInstance().getAccountId() + "")
                .enqueue(new ApiCallBack<UserInfo>(activity.get()) {
                    @Override
                    public void onApiResponse(Call<UserInfo> call, Response<UserInfo> response) {
                        super.onApiResponse(call, response);
                        UserInfo body = response.body();
                        if (body != null) {
                            // 用户信息获取成功
                            UserManager.getInstance().setWeight(body.getWeight());
                            UserManager.getInstance().setHeight(body.getHeight());
                            UserManager.getInstance().setName(body.getName());
                            UserManager.getInstance().setBirth(body.getBirthday());
                            if("MAN".equals(body.getGender())){
                                UserManager.getInstance().setSex("男");
                            }else{
                                UserManager.getInstance().setSex("女");
                            }
                            UserManager.getInstance().setAddr(body.getAddr());
                            UserManager.getInstance().setCardId(body.getCardId());
                            UserManager.getInstance().setHeadUrl(body.getHeadUrl());
                            UserManager.getInstance().setPhone(body.getPhone());
                            Logger.i("获取信息" + body.getWeight() + "");

                            callBack.result(0, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfo> call, Throwable t) {
                        super.onFailure(call, t);
                        callBack.result(-1, t.getMessage());
                    }
                });
    }


    public void eidtSimpleUserInfo(WeakReference<Activity> activity, final NetCallBack callBack) {
        if (activity.get() != null) {
            if (!NetStateController.isNetworkConnected(activity.get())) {
                Toast.makeText(activity.get(), activity.get().getResources().getString(com.guider.glu.R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SimpleUserInfo simpleUserInfo = new SimpleUserInfo();
        simpleUserInfo.setAccountId(UserManager.getInstance().getAccountId());

        simpleUserInfo.setAddr(UserManager.getInstance().getAddr());
        simpleUserInfo.setBirthday(UserManager.getInstance().getBirth() + "T00:00:00Z");
        simpleUserInfo.setCardId(UserManager.getInstance().getCardId());
        simpleUserInfo.setGender(UserManager.getInstance().getSex().equals("男") ? "MAN" : "WOMAN ");
        simpleUserInfo.setHeadUrl(UserManager.getInstance().getHeadUrl());
        simpleUserInfo.setPhone(UserManager.getInstance().getPhone());
        simpleUserInfo.setHeight(UserManager.getInstance().getHeight());
        simpleUserInfo.setWeight(UserManager.getInstance().getWeight());
        simpleUserInfo.setName(UserManager.getInstance().getName());

        ApiUtil.createApi(IGuiderApi.class , false).editSimpUserInfo(simpleUserInfo)
                .enqueue(new ApiCallBack<SimpleUserInfo>(activity.get()) {
                    @Override
                    public void onApiResponse(Call<SimpleUserInfo> call, Response<SimpleUserInfo> response) {
                        super.onApiResponse(call, response);
                        callBack.result(0, null);
                    }

                    @Override
                    public void onFailure(Call<SimpleUserInfo> call, Throwable t) {
                        callBack.result(-1, t.getMessage());
                    }
                });
    }

    public void setNonbsSet(WeakReference<Activity> activityWeakReference, final NetCallBack operatorCallBack){
        if (activityWeakReference.get() != null) {
            if (!NetStateController.isNetworkConnected(activityWeakReference.get())) {
                Toast.makeText(activityWeakReference.get(), "没有网络, 请打开网络", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        NonbsBean nonbsBean = new NonbsBean();
        nonbsBean.setAbnormal(!BodyIndex.Normal.equals(BodyIndex.getInstance().getDiabetesType()));
        nonbsBean.setAccountId(UserManager.getInstance().getAccountId());
        nonbsBean.setCreateTime(BodyIndex.getInstance().getCreateTime());
        nonbsBean.setEnzyme("1".equals(BodyIndex.getInstance().getGlucosedesesSate()));
        nonbsBean.setId(BodyIndex.getInstance().getId());
        nonbsBean.setPhmb("1".equals(BodyIndex.getInstance().getBiguanidesState()));
        nonbsBean.setUnTake(!BodyIndex.getInstance().isEatmedicine());
        nonbsBean.setUpdateTime(BodyIndex.getInstance().getUpdateTime());
        nonbsBean.setUrea("1".equals(BodyIndex.getInstance().getSulphonylureasState()));
        nonbsBean.setValue(BodyIndex.getInstance().getValue());
        ApiUtil.createHDApi(IUserHDApi.class).setNonbs(nonbsBean).enqueue(new ApiCallBack<NonbsBean>() {
            @Override
            public void onApiResponse(Call<NonbsBean> call, Response<NonbsBean> response) {
                super.onApiResponse(call, response);
                operatorCallBack.result(0, null);
            }

            @Override
            public void onFailure(Call<NonbsBean> call, Throwable t) {
                operatorCallBack.result(-1, "");
            }
        });
    }

    public void getNonbsSet(WeakReference<Activity> activityWeakReference, final NetCallBack operatorCallBack) {
        if (activityWeakReference.get() != null) {
            if (!NetStateController.isNetworkConnected(activityWeakReference.get())) {

                Toast.makeText(activityWeakReference.get(), activityWeakReference.get().getResources().getString(com.guider.glu.R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ApiUtil.createHDApi(IUserHDApi.class).getNonbs(UserManager.getInstance().getAccountId()).enqueue(new ApiCallBack<NonbsBean>(){
            @Override
            public void onApiResponse(Call<NonbsBean> call, Response<NonbsBean> response) {
                super.onApiResponse(call, response);
                NonbsBean body = response.body();
                BodyIndex.getInstance().setEatmedicine(!body.isUnTake());
                BodyIndex.getInstance().setGlucosedesesSate(body.isEnzyme() ? "1" : "0");
                BodyIndex.getInstance().setBiguanidesState(body.isPhmb() ? "1" : "0");
                BodyIndex.getInstance().setSulphonylureasState(body.isUrea() ? "1" : "0");
                //abnormal == flase 为展示页面
                BodyIndex.getInstance().setDiabetesType(body.isAbnormal() ? "" : BodyIndex.Normal);
                BodyIndex.getInstance().setCreateTime(body.getCreateTime());
                BodyIndex.getInstance().setUpdateTime(body.getUpdateTime());
                BodyIndex.getInstance().setValue(body.getValue());
                BodyIndex.getInstance().setId(body.getId());

                operatorCallBack.result(0, null);
            }

            @Override
            public void onFailure(Call<NonbsBean> call, Throwable t) {
                operatorCallBack.result(-1, "");
            }
        });
    }

    public void getHealthRange(WeakReference<Activity> activityWeakReference) {
        if (activityWeakReference.get() != null) {
            if (!NetStateController.isNetworkConnected(activityWeakReference.get())) {

                Toast.makeText(activityWeakReference.get(), activityWeakReference.get().getResources().getString(com.guider.glu.R.string.no_network), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ApiUtil.createHDApi(IUserHDApi.class).getHealthrange(UserManager.getInstance().getAccountId()).enqueue(new ApiCallBack<Healthrange>(){
            @Override
            public void onApiResponse(Call<Healthrange> call, Response<Healthrange> response) {
                super.onApiResponse(call, response);
                Healthrange body = response.body();
                if (body == null) {
                    return;
                }
                HealthRange.getInstance().setSbpIdealMin(body.getSbpIdealMin());
                HealthRange.getInstance().setSbpIdealMax(body.getSbpIdealMax());
                HealthRange.getInstance().setDbpIdealMin(body.getDbpIdealMin());
                HealthRange.getInstance().setDbpIdealMax(body.getDbpIdealMax());
                HealthRange.getInstance().setSbpHMin(body.getSbpHMin());
                HealthRange.getInstance().setDbpHMin(body.getDbpHMin());
                HealthRange.getInstance().setFbsMin(body.getFbsMin());
                HealthRange.getInstance().setFbsMax(body.getFbsMax());
                HealthRange.getInstance().setPbsMax(body.getPbsMax());
                HealthRange.getInstance().setPbsMin(body.getPbsMin());
                HealthRange.getInstance().setBmiMin(body.getBmiMin());
                HealthRange.getInstance().setBmiMax(body.getBmiMax());
                HealthRange.getInstance().setBoMin(body.getBoMin());
                HealthRange.getInstance().setHrMin(body.getHrMin());
                HealthRange.getInstance().setHrMax(body.getHrMax());
            }
        });

    }


    private Dialog dialog;
    private RotateAnimation baseAlphaAnimation;
    private View baseTextview;

    protected void showDialog(WeakReference<Activity> _mActivity, String msg){
        if (dialog != null && dialog.isShowing()){
            return;
        }
        if (_mActivity.get() == null){
            return;
        }
        dialog = new Dialog(_mActivity.get());
        //去掉标题线
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(com.guider.health.common.R.layout.common_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        baseTextview = dialog.findViewById(com.guider.health.common.R.id.loading);
        TextView tvMsg = dialog.findViewById(com.guider.health.common.R.id.msg);
        if (msg == null) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(msg);
        }
        //背景透明
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        baseAnimation();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER; // 居中位置
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(com.guider.health.common.R.style.mystyle);  //添加动画
    }

    protected void showDialog(WeakReference<Activity> _mActivity){
        showDialog(_mActivity, null);
    }


    protected void hideDialog(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
            if (baseAlphaAnimation != null){
                baseAlphaAnimation.cancel();
            }
        }

    }

    public void baseAnimation(){
        baseAlphaAnimation=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);//设置图片动画属性，各参数说明可参照api
        baseAlphaAnimation.setRepeatCount(-1);//设置旋转重复次数，即转几圈
        baseAlphaAnimation.setDuration(1000);//设置持续时间，注意这里是每一圈的持续时间，如果上面设置的圈数为3，持续时间设置1000，则图片一共旋转3秒钟
        baseAlphaAnimation.setInterpolator(new LinearInterpolator());//设置动画匀速改变。相应的还有AccelerateInterpolator、DecelerateInterpolator、CycleInterpolator等
        baseTextview.setAnimation(baseAlphaAnimation);//设置imageview的动画，也可以myImageView.startAnimation(myAlphaAnimation)
        baseAlphaAnimation.setAnimationListener(new Animation.AnimationListener() { //设置动画监听事件
            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }
            //图片旋转结束后触发事件，这里启动新的activity
            @Override
            public void onAnimationEnd(Animation arg0) {
                // TODO Auto-generated method stub
//                Intent i2 = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(i2);
            }
        });

        baseAlphaAnimation.startNow();
    }
}
