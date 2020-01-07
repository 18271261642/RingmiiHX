package com.guider.health.bp.view;

import android.view.View;

import com.guider.health.common.core.BaseFragment;
import com.guider.health.common.core.Config;
import com.guider.health.common.core.RouterPathManager;

import me.yokeyword.fragmentation.ISupportFragment;

public class FinishClick implements View.OnClickListener {

    BaseFragment fragment;

    public FinishClick(BaseFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        if (RouterPathManager.Devices.size() > 0) {

            String fragmentPath = RouterPathManager.Devices.remove();

            try {
                fragment.popTo(Class.forName(Config.HOME_DEVICE), false);

                fragment.start((ISupportFragment) Class.forName(fragmentPath).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            try {

                fragment.popTo(Class.forName(Config.HOME_DEVICE), false);
                fragment.start((ISupportFragment) Class.forName(Config.END_FRAGMENT).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
