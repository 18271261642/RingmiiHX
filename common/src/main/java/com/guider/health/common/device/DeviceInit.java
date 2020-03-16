package com.guider.health.common.device;

import android.util.ArrayMap;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.apilib.model.Devices;
import com.guider.health.common.R;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.Glucose;
import com.guider.health.common.core.HearRate;
import com.guider.health.common.core.HearRate12;
import com.guider.health.common.core.HearRateHd;
import com.guider.health.common.core.HeartPressAve;
import com.guider.health.common.core.HeartPressBp;
import com.guider.health.common.core.HeartPressCx;
import com.guider.health.common.core.HeartPressMbb_88;
import com.guider.health.common.core.HeartPressMbb_9804;
import com.guider.health.common.core.HeartPressYf;
import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.standard.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by haix on 2019/7/24.
 */

public class DeviceInit {

    public static final String DEV_ECG_6 = "CmateHv1.0.0";
    public static final String DEV_GLU = "BDE_WEIXIN_TTMv1.0.0";
    public static final String DEV_BP = "FORA P30 PLUSv1.0.0";
    public static final String DEV_BP_CX = "CXBPv1.0.0";
    public static final String DEV_BP_YF = "YFKJv1.0.0";
    public static final String DEV_BP_AVE = "AVE-2000v1.0.0";
    public static final String DEV_BP_MBB_88 = "RBP1810130249v1.0.0";
    public static final String DEV_BP_MBB_9804 = "BP06D21905750026v1.0.0";
    public static final String DEV_ECG_12 = "WWKECGv1.0.0";
    public static final String DEV_ECG_tzq = "device_tzqv1.0.0";
    public static final String DEV_ECG_HD = "device_hdv1.0.0";

    protected DeviceInit() {}
    private volatile static DeviceInit instance;
    public static DeviceInit getInstance() {
        if (instance != null) {
            return instance;
        }
        if (instance == null) {
            synchronized (DeviceInit.class) {
                if (instance == null) {
                    instance = new DeviceInit();
                }
            }
        }
        return instance;
    }


    public final ArrayMap<String, Integer> pics = new ArrayMap<>();

    public final ArrayMap<String, String> fragments = new ArrayMap<>();

    public final ArrayMap<String, String> names = new ArrayMap<>();

    private String type = "";

    public void setType(String type) {
        if (Constant.isDebug) {
            this.type = "test";
            return;
        }
        this.type = type;
    }

    public String getType(){
        return type;
    }


    public void setDeviceTag(String text, boolean b) {

        switch (text) {
            case DEV_BP:   // 移动版
                HeartPressBp.getInstance().setTag(b);
                break;
            case DEV_BP_MBB_88:   // 脉搏波(小的)
                HeartPressMbb_88.getInstance().setTag(b);
                break;
            case DEV_BP_MBB_9804:   // 脉搏波 (大的)
                HeartPressMbb_9804.getInstance().setTag(b);
                break;
            case DeviceInit.DEV_BP_CX:            // 插线版
                HeartPressCx.getInstance().setTag(b);
                break;
            case DEV_BP_YF:            // 云峰
                HeartPressYf.getInstance().setTag(b);
                break;
            case DEV_BP_AVE:        // AVE
                HeartPressAve.getInstance().setTag(b);
                break;
            case DEV_ECG_6:          // 6导
                HearRate.getInstance().setTag(b);
                break;
            case DEV_ECG_HD:          // 6导
                HearRateHd.getInstance().setTag(b);
                break;
            case DEV_GLU:  // 血糖
                Glucose.getInstance().setTag(b);
                break;
            case DEV_ECG_12:          // 12导
                HearRate12.getInstance().setTag(b);
                break;
        }
    }

    public void setDeviceTagFalse() {

        Glucose.getInstance().setTag(false);
        HearRate.getInstance().setTag(false);
        HeartPressBp.getInstance().setTag(false);
        HeartPressCx.getInstance().setTag(false);
        HeartPressYf.getInstance().setTag(false);
        HeartPressAve.getInstance().setTag(false);
        HeartPressMbb_9804.getInstance().setTag(false);
        HeartPressMbb_88.getInstance().setTag(false);
        HearRate12.getInstance().setTag(false);

    }

    public void init(final OnHasDeviceList callback) {
        if (Config.DEVICE_KEYS.size() > 0) {
            callback.onHaveList();
            return;
        }
        pics.put(DEV_GLU, R.mipmap.device_wcxt);
        pics.put(DEV_ECG_6, R.mipmap.device_ldxd);
        pics.put(DEV_ECG_HD, R.mipmap.device_ldxd);
        pics.put(DEV_BP, R.mipmap.device_dp);
        pics.put(DEV_BP_CX, R.mipmap.device_cx);
        pics.put(DEV_BP_YF, R.mipmap.device_yf);
        pics.put(DEV_BP_AVE, R.mipmap.device_ave);
        pics.put(DEV_ECG_12, R.mipmap.dao12_device_choose);
        pics.put(DEV_ECG_tzq, R.mipmap.device_tzq);
        pics.put(DEV_BP_MBB_88  , R.mipmap.device_mbb_88);
        pics.put(DEV_BP_MBB_9804, R.mipmap.device_mbb_98);

        fragments.put(DEV_GLU, Config.GLU_FRAGMENT);
        fragments.put(DEV_ECG_6, Config.ECG_FRAGMENT);
        fragments.put(DEV_ECG_HD, Config.ECG_HD_FRAGMENT);
        fragments.put(DEV_BP, Config.BP_FRAGMENT);
        fragments.put(DEV_BP_CX, Config.BP_CX_FRAGMENT);
        fragments.put(DEV_BP_YF, Config.BP_YF_FRAGMENT);
        fragments.put(DEV_BP_AVE, Config.BP_AVE_FRAGMENT);
        fragments.put(DEV_ECG_12, Config.DAO12_FRAGMENT);
        fragments.put(DEV_ECG_tzq, Config.ECG_ZANG_YIN);
        fragments.put(DEV_BP_MBB_88  , Config.BP_MBB88_FRAGMENT);
        fragments.put(DEV_BP_MBB_9804, Config.BP_MBB9804_FRAGMENT);

        names.put(DEV_ECG_6, MyUtils.application.getString(R.string.liudao));
        names.put(DEV_BP, MyUtils.application.getString(R.string.fore));
        names.put(DEV_GLU, "无创血糖测量");
        names.put(DEV_ECG_HD, "红豆心电");
        names.put(DEV_BP_CX, "臂筒式血压测量");
        names.put(DEV_BP_YF, "动脉硬化测量");
        names.put(DEV_BP_AVE, "动脉硬化测量");
        names.put(DEV_ECG_12, "十二导心电测量");
        names.put(DEV_ECG_tzq, "远程脏音测量");
        names.put(DEV_BP_MBB_88  , "脉搏波88");
        names.put(DEV_BP_MBB_9804, "脉搏波9804");

        Config.DEVICE_KEYS.add(DEV_GLU);
        Config.DEVICE_KEYS.add(DEV_BP);

        if (callback != null) {
            callback.needLoad();
        }
        ApiUtil.createHDApi(IGuiderApi.class).getDeviceList(MyUtils.getMacAddress()).enqueue(
                new ApiCallBack<List<Devices>>(){
                    @Override
                    public void onApiResponse(Call<List<Devices>> call, Response<List<Devices>> response) {
                        super.onApiResponse(call, response);
                        if (response != null && response.body() != null && !response.body().isEmpty()) {
                            Config.DEVICE_KEYS.clear();
                            for (Devices device : response.body()) {
                                Config.DEVICE_KEYS.add(device.getBtName() + device.getVersion());
                                Config.DEVICE_OBJ.put(device.getBtName() + device.getVersion(), device);
                            }
                        }
                    }

                    @Override
                    public void onRequestFinish() {
                        super.onRequestFinish();
                        if (callback != null) {
                            callback.onHaveList();
                        }
                    }
                }
        );
    }

    public interface OnHasDeviceList {

        void needLoad();
        void onHaveList();
    }

}
