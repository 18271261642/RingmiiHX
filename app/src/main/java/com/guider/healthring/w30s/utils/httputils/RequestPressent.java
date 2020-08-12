package com.guider.healthring.w30s.utils.httputils;

import android.content.Context;
import com.guider.healthring.siswatch.utils.WatchUtils;


/**
 * Created by Administrator on 2018/4/3.
 */

public class RequestPressent {

    private RequestView requestView;

    private RequestModel requestModel;

    public RequestPressent() {
        requestModel = new RequestModel();
    }

    public void getRequestJSONObject(final int what, String url, Context mContext,
                                     String jsonMap, final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null)
                requestModel.getJSONObjectModelData(url, mContext, jsonMap, s -> {
                    if (!WatchUtils.isEmpty(s)) {
                        if (requestView != null) requestView.successData(what, s, days);
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
        }
    }


    //Put
    public void getRequestPutJsonObject(final int what, String url, Context context,
                                        String jsonMap, final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null) {
                requestModel.getPutJSONObjectModelData(url, context, jsonMap, s -> {
                    if (!WatchUtils.isEmpty(s)) {
                        if (requestView != null) requestView.successData(what, s, days);
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
            }
        }
    }


    public void getRequestJSONObjectGet(final int what, String url,
                                        Context mContext, final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null)
                requestModel.getJSONObjectModelDataGet(url, mContext, s -> {
                    if (!WatchUtils.isEmpty(s)) {
                        if (requestView != null) requestView.successData(what, s, days);
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
        }
    }


    public void getRequestJSONObject(final int what, String url, Context mContext, final int days) {
        if (requestView != null) {
            requestView.showLoadDialog(what);
            if (requestModel != null)
                requestModel.getJSONObjectModelData(url, mContext, s -> {
                    if (!WatchUtils.isEmpty(s)) {
                        if (requestView != null) {
                            requestView.successData(what, s, days);
                        }
                    }
                }, new CustumListener() {
                    @Override
                    public void onCompleted() {
                        if (requestView != null) {
                            requestView.closeLoadDialog(what);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestView != null) requestView.failedData(what, e);
                    }
                });
        }
    }


    //绑定
    public void attach(RequestView requestView) {
        this.requestView = requestView;
    }

    //解除绑定
    public void detach() {
        if (requestView != null) {
            requestView = null;
        }
    }
}
