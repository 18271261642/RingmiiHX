package com.guider.health.common.utils;

import android.view.View;

import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.RouterPathManager;
import com.guider.health.common.device.MeasureDeviceManager;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

public class SkipClick implements View.OnClickListener {

    BaseFragment fragment;
    String deviceName;
    boolean isOtherTask;
    public static final int CODE_SKIP = 10001;

    public SkipClick(BaseFragment fragment, String deviceName) {
        this(fragment , deviceName , false);
    }

    public SkipClick(BaseFragment fragment , String deviceName , boolean isOtherTask) {
        this.fragment = fragment;
        this.deviceName = deviceName;
        this.isOtherTask = isOtherTask;
    }

    @Override
    public void onClick(View v) {
        if (isOtherTask) {
            fragment.getActivity().setResult(SkipClick.CODE_SKIP);
            fragment.getActivity().finish();
        } else {
            skip();
        }
    }

    public void skip() {
        new MeasureDeviceManager().removeDeviceFromList(deviceName);
        if (RouterPathManager.Devices.size() > 0) {

            String fragmentPath = RouterPathManager.Devices.remove();

            try {
                fragment.popTo(Class.forName(Config.HOME_DEVICE), false);

                fragment.start((ISupportFragment) Class.forName(fragmentPath).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            List<String> measureDevices = new MeasureDeviceManager().getMeasureDevices();
            try {
                fragment.popTo(Class.forName(Config.HOME_DEVICE), false);
                if (measureDevices.size() > 0) {
                    // 有设备测量并且有结果
                    fragment.start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
