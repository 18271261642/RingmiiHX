package com.guider.libbase.other;

import android.content.DialogInterface;
import android.view.View;

/**
 * 自定义dialog的抽象
 * Created by Administrator on 2016/8/1 0001.
 */
public interface IOnDialogClick
{
    /**
     * view的初始化
     * @param view
     */
    public void onInit(DialogInterface dialogInterface, View view);
    /**
     * 点击确认
     * @param view
     */
    public void onConfirm(DialogInterface dialogInterface, View view);
    /**
     * 点击确认,输入框
     * @param view
     */
    public void onConfirm(DialogInterface dialogInterface, View view, String value);
    /**
     * 点击取消
     * @param view
     */
    public void onCancle(DialogInterface dialogInterface, View view);
}
