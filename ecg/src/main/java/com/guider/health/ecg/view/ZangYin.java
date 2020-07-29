package com.guider.health.ecg.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IUserHDApi;
import com.guider.health.apilib.model.Stethoscope;
import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.core.UserManager;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.device.MeasureDeviceManager;
import com.guider.health.common.net.util.file.CardiartAppUtil;
import com.guider.health.common.utils.SkipClick;
import com.guider.health.ecg.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Date;

import me.yokeyword.fragmentation.ISupportFragment;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 脏音听诊器
 */
public class ZangYin extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.zangyin_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TextView) view.findViewById(R.id.title)).setText("脏音听诊器测量");
        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_tzq));
        if (savedInstanceState != null) {
            return;
        }
        showDialog();
        initView();

        boolean skipSuccess = new CardiartAppUtil().skipToApp(this);
        if (!skipSuccess) {
            Toast.makeText(_mActivity, "未安装应用", Toast.LENGTH_SHORT).show();
            toNext();
        }
    }

    private void initView() {
        setHomeEvent(view.findViewById(R.id.home), Config.HOME_DEVICE);
        ((TextView) view.findViewById(R.id.title)).setText("蓝牙连接");
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }


    String testTime;
    String measureMode;
    int measurePart;
    int measurePoint;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("听诊器返回 " + requestCode + "  resultCode=" + resultCode);
        hideDialog();
        if (requestCode == 992 && data != null) {
            testTime = data.getStringExtra("testTime");
            measureMode = data.getStringExtra("measureMode");
            measurePart = data.getIntExtra("measurePart", -1);
            measurePoint = data.getIntExtra("measurePoint", -1);
            String filePath = data.getStringExtra("filePath");

            inflaterData();
            showDialog("正在提交数据...");
            updateFile(filePath);
        } else {
            // todo 没有拿到数据
            toNext();
        }
    }

    private void inflaterData() {

    }

    private void updateFile(String filePath) {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + filePath;
        Logger.i("开始上传文件 : " + filePath);
        ApiUtil.uploadFile(_mActivity, filePath, new ApiCallBack<String>(_mActivity) {
            @Override
            public void onApiResponse(Call<String> call, Response<String> response) {
                super.onApiResponse(call, response);
                // todo 成功之后上传数据
                Logger.i("文件上传成功 " + response.body());
                updateDate(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                Logger.i("文件上传失败 " + t.getMessage());
                toNext();
            }
        });
    }

    private void updateDate(String body) {
        Stethoscope stethoscope = new Stethoscope();
        stethoscope.setDeviceCode(MyUtils.getMacAddress());
        stethoscope.setAccountId(UserManager.getInstance().getAccountId());
        stethoscope.setTestTime(new Date());
        stethoscope.setMeasureMode(measureMode);
        stethoscope.setMeasurePart(measurePart + "");
        stethoscope.setMeasurePoint(measurePoint + "");
        stethoscope.setAudioUrl(body);
        Logger.i("开始上传数据 : " + new Gson().toJson(stethoscope));
        ApiUtil.createHDApi(IUserHDApi.class)
                .uploadStethoscope(stethoscope)
                .enqueue(new ApiCallBack<Stethoscope>(_mActivity) {
                    @Override
                    public void onApiResponse(Call<Stethoscope> call, Response<Stethoscope> response) {
                        super.onApiResponse(call, response);
                        // todo 成功之后关闭
                        Logger.i("听诊器数据提交成功 " + response.body().getAudioUrl());
                        toNext();
                    }

                    @Override
                    public void onFailure(Call<Stethoscope> call, Throwable t) {
                        super.onFailure(call, t);
                        Logger.i("数据提交失败 " + t.getMessage());
                        toNext();
                    }
                });
    }

    private void toNext() {
        hideDialog();
        if (RouterPathManager.Devices.size() > 0) {

            String fragmentPath = RouterPathManager.Devices.remove();
            try {
                this.popTo(Class.forName(Config.HOME_DEVICE), false);

                this.start((ISupportFragment) Class.forName(fragmentPath).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            try {
                if (check()) {
                    this.popTo(Class.forName(Config.HOME_DEVICE), false);
                    this.start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
                } else {
                    pop();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    boolean check() {
        return new MeasureDeviceManager().getMeasureDevices().size() > 1;
    }
}
