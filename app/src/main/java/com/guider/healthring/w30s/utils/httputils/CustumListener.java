package com.guider.healthring.w30s.utils.httputils;

/**
 * Created by Administrator on 2018/4/3.
 */

public interface CustumListener {

    void onCompleted();

    void onError(Throwable e);
}
