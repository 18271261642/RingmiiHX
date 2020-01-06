package com.guider.health.common.net.net.callback;

/**
 * Created by jett on 2018/6/6.
 *
 *
 * 用来作文件上传下载时, 判断有没有完成
 */

public interface IRequest {
    //请求开始前
    void onRequestStart();
    //请求结束后
    void onRequestEnd();
}
