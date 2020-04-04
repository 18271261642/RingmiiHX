package com.guider.libbase.other.impl;

import android.content.DialogInterface;
import android.view.View;

import com.guider.libbase.other.IOnDialogClick;


/**
 * Created by donggang on 2016/8/1 0001.
 */
public class BaseOnDialogClick implements IOnDialogClick
{

    /**
     * view的初始化
     *
     * @param dialogInterface
     * @param view
     */
    @Override
    public void onInit(DialogInterface dialogInterface, View view)
    {

    }

    /**
     * 点击确认
     *
     * @param dialogInterface
     * @param view
     */
    @Override
    public void onConfirm(DialogInterface dialogInterface, View view)
    {

    }

    /**
     * 点击确认,输入框
     *
     * @param dialogInterface
     * @param view
     * @param value
     */
    @Override
    public void onConfirm(DialogInterface dialogInterface, View view, String value)
    {

    }

    /**
     * 点击取消
     *
     * @param dialogInterface
     * @param view
     */
    @Override
    public void onCancle(DialogInterface dialogInterface, View view)
    {

    }
}
