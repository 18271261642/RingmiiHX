package com.guider.health.common.net.net.callback;

/**
 * Created by jett on 2018/6/6.
 */
public interface IFailure {
    //这个和IError的区别是  IError是连接到服务器了, 服务器返回了错误信息,
    //这个是服务器就没有连接上, 所以也没有返回信息, 直接在这里抛出异常
    void onFailure();
}
