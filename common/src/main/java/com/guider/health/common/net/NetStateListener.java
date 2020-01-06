package com.guider.health.common.net;

/**
 * Created by haix on 2019/7/11.
 */

public interface NetStateListener {
    /**
     * 当网络类型发生改变的时候回调
     *
     * @param type 网络类型
     */
    public void onNetworkState(NetStateController.NetworkType type);
}
