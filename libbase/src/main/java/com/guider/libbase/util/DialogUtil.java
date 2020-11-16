package com.guider.libbase.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.R;
import com.guider.libbase.other.IOnDialogClick;
import com.guider.libbase.other.impl.BaseOnDialogClick;


/**
 * 对话框工具类
 * Created by donggang on 2016/8/1 0001.
 */
public class DialogUtil
{
    public static AlertDialog showConfirmDialog(Context context, ViewGroup viewRoot,
                                         final String msg,
                                         final IOnDialogClick iOnDialogClick,
                                         final boolean dismissAfterConfirm)
    {
        return showBaseDialog(context, viewRoot, R.layout.dialog_content_confirm,
                "提示",
                "取消",
                "确定",
                new BaseOnDialogClick()
                {
                    @Override
                    public void onInit(DialogInterface dialogInterface, View view)
                    {
                        TextView tviewMsg = (TextView) view.findViewById(R.id.tview_msg);
                        tviewMsg.setText(msg);
                    }

                    @Override
                    public void onCancle(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                            iOnDialogClick.onCancle(dialogInterface, view);
                    }

                    @Override
                    public void onConfirm(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                            iOnDialogClick.onConfirm(dialogInterface, view);
                    }
                },
                dismissAfterConfirm);
    }

    public static void showConfirmDialog(Context context, ViewGroup viewRoot,
                                         String  title,
                                         final String msg,
                                         String confirm,
                                         final IOnDialogClick iOnDialogClick)
    {
        showConfirmDialog(context, viewRoot,
                title,
                msg,
                "", confirm,
                iOnDialogClick,
                true);
    }

    public static AlertDialog showConfirmDialog(Context context, ViewGroup viewRoot,
                                         String  title,
                                         final String msg,
                                         String cancle, String confirm,
                                         final IOnDialogClick iOnDialogClick,
                                         final boolean dismissAfterConfirm)
    {
        return showBaseDialog(context, viewRoot, R.layout.dialog_content_confirm,
                title,
                cancle,
                confirm,
                new BaseOnDialogClick()
                {
                    @Override
                    public void onInit(DialogInterface dialogInterface, View view)
                    {
                        TextView tviewMsg = (TextView) view.findViewById(R.id.tview_msg);
                        tviewMsg.setText(msg);
                    }

                    @Override
                    public void onCancle(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                            iOnDialogClick.onCancle(dialogInterface, view);
                    }

                    @Override
                    public void onConfirm(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                            iOnDialogClick.onConfirm(dialogInterface, view);
                    }
                },
                dismissAfterConfirm);
    }

    public static void showConfirmDialog(Context context, ViewGroup viewRoot,
                                         String  title,
                                         final String msg,
                                         String cancle, String confirm)
    {
        showBaseDialog(context, viewRoot, R.layout.dialog_content_confirm,
                title,
                cancle,
                confirm,
                new BaseOnDialogClick()
                {
                    @Override
                    public void onInit(DialogInterface dialogInterface, View view)
                    {
                        TextView tviewMsg = (TextView) view.findViewById(R.id.tview_msg);
                        tviewMsg.setText(msg);
                    }
                },
                true);
    }

    public static void showInputDialog(Context context, ViewGroup viewRoot,
                                       String  title,
                                       String cancle, String confirm,
                                       final IOnDialogClick iOnDialogClick)
    {
        showBaseDialog(context, viewRoot, R.layout.dialog_content_input,
                title,
                cancle,
                confirm,
                iOnDialogClick,
                true);
    }

    public static void showInputDialog(Context context, ViewGroup viewRoot,
                                       String  title,
                                       final String defaultValue,
                                       final IOnDialogClick iOnDialogClick)
    {
        showBaseDialog(context, viewRoot, R.layout.dialog_content_input,
                title,
                "取消",
                "确认",
                new BaseOnDialogClick()
                {
                    @Override
                    public void onInit(DialogInterface dialogInterface, View view)
                    {
                        TextView tviewMsg = (TextView) view.findViewById(R.id.etext_input);
                        tviewMsg.setText(defaultValue);
                    }

                    @Override
                    public void onCancle(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                            iOnDialogClick.onCancle(dialogInterface, view);
                    }

                    @Override
                    public void onConfirm(DialogInterface dialogInterface, View view)
                    {
                        if (null != iOnDialogClick)
                        {
                            TextView tviewMsg = (TextView) view.findViewById(R.id.etext_input);

                            iOnDialogClick.onConfirm(dialogInterface, view, tviewMsg.getText().toString());
                        }
                    }
                },
                true);
    }

    public static void showInputDialog(Context context, ViewGroup viewRoot,
                                       String  title,
                                       String cancle, String confirm,
                                       final IOnDialogClick iOnDialogClick,
                                       final boolean dismissAfterConfirm)
    {
        showBaseDialog(context, viewRoot, R.layout.dialog_content_input,
                title,
                cancle,
                confirm,
                iOnDialogClick,
                dismissAfterConfirm);
    }

    public static AlertDialog showBaseDialog(Context context, ViewGroup viewRoot,
                                         int contentLayoutId,
                                         String  title,
                                         String cancle, String confirm,
                                        final IOnDialogClick iOnDialogClick,
                                        final boolean dismissAfterConfirm)
    {
        return showBaseDialog(context, viewRoot, R.layout.dialog_base,
                R.id.llayout_root, contentLayoutId,
                title, R.id.tview_title,
                cancle, R.id.button_cancle,
                confirm, R.id.button_confirm,
                iOnDialogClick,
                dismissAfterConfirm);
    }

    public static AlertDialog showConfirmDialog(Context context,
                                      String title,
                                      String confirm,
                                      final IOnDialogClick iOnDialogClick,
                                      final boolean dismissAfterConfirm)
    {
        return showBaseDialog(context, null, R.layout.dialog_confirm,
                R.id.llayout_root, R.layout.dialog_content_confirm,
                title, R.id.tview_title,
                "", -1,
                confirm, R.id.button_confirm,
                iOnDialogClick,
                dismissAfterConfirm);
    }

    public static void showConfirmDialog(Context context,
                                         String msg, IOnDialogClick iOnDialogClick)
    {
        showConfirmDialog(context, null,
                "提示",
        msg,
        "", "确定",
                iOnDialogClick,
        true);
    }

    /**
     * 显示标准对话框
     * @param context 上下文环境
     * @param viewRoot view的根
     * @param customViewId 自定义view的id
     * @param contentViewRoot 自定义view包裹的view的根
     * @param contentViewId 自定义的view包裹的view的id
     * @param title 标题
     * @param tviewTitleId 标题的view的id
     * @param cancle 取消按钮内容
     * @param buttonCancleId 取消按钮的id
     * @param confirm 确认按钮内容
     * @param buttonConfirmId 确认按钮的id
     * @param iOnDialogClick view点击的回调
     * @param dismissAfterConfirm 点击确认是否要dismiss
     */
    private static AlertDialog showBaseDialog(Context context, ViewGroup viewRoot,
                                         int customViewId, int contentViewRoot,
                                         int contentViewId,
                                         String  title, int tviewTitleId,
                                         String cancle, int buttonCancleId,
                                         String confirm, int buttonConfirmId,
                                         final IOnDialogClick iOnDialogClick,
                                         final boolean dismissAfterConfirm)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        View customView = LayoutInflater.from(context).inflate(customViewId, viewRoot);
        ViewGroup viewGroup = (ViewGroup) customView.findViewById(contentViewRoot);
        final View contentView = LayoutInflater.from(context).inflate(contentViewId, viewGroup);
        if (null != iOnDialogClick)
            iOnDialogClick.onInit(alertDialog, contentView);

        Window mWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        alertDialog.setView(customView, 0, 0, 0, 0);
        alertDialog.onWindowAttributesChanged(lp);
        TextView tviewTitle = (TextView) customView.findViewById(tviewTitleId);
        tviewTitle.setText(title);
        Button buttonConfirm = (Button) customView.findViewById(buttonConfirmId);
        buttonConfirm.setText(confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (null != iOnDialogClick)
                    iOnDialogClick.onConfirm(alertDialog, contentView);
                if (dismissAfterConfirm)
                    alertDialog.dismiss();
            }
        });

        if (-1 != buttonCancleId || !StringUtil.isEmpty(cancle))
        {
            Button buttonCancle = (Button) customView.findViewById(buttonCancleId);
            buttonCancle.setText(cancle);
            buttonCancle.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    if (null != iOnDialogClick)
                        iOnDialogClick.onCancle(alertDialog, contentView);

                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.show();
        return alertDialog;
    }
}
