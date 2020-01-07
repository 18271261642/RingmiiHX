package com.guider.health.adapter;

public interface RequestCallback {

    public static final int CODE_OK = 0;
    public static final int CODE_FAIL = -1;

    /**
     * 当结果上传成功后会调用该回调
     * @param name 设备名称
     * @param msg 返回结果消息
     * @param code 0为成功 , -1为失败
     */
    void onRequestFinish(String name, String msg, int code);

}
