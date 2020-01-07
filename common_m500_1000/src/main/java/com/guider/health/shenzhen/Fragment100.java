package com.guider.health.shenzhen;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.guider.health.common.core.BaseFragment;

/**
 * Created by haix on 2019/7/31.
 */

public class Fragment100 extends BaseFragment implements MemberInterface{


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public Activity getMemberActivity() {
        return _mActivity;
    }
}
