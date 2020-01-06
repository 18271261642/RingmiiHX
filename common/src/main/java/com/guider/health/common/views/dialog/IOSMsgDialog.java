package com.guider.health.common.views.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.guider.health.common.R;


/**
 * 无法开始训练弹窗
 * Created by luziqi on 2018/2/8.
 */
public class IOSMsgDialog extends BaseDialog {

    private View view;
    private View yes;
    private TextView buttonTextView;
    private String msg;
    private String buttonText;

    public IOSMsgDialog(Context context , String msg) {
        super(context);
        this.msg = msg;
        buttonText = context.getResources().getString(R.string.ui_sure);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_ios_msg, null);
        TextView msg = (TextView) view.findViewById(R.id.msg);
        msg.setText(this.msg);
        buttonTextView = (TextView) view.findViewById(R.id.yes);
        buttonTextView.setText(buttonText);
        yes = view.findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYeaClick != null) {
                    onYeaClick.onClick();
                }
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void setUiBeforShow() {

    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        if (buttonTextView != null) {
            buttonTextView.setText(buttonText);
        }
    }

    private OnButtonClick onYeaClick;
    public IOSMsgDialog setOnYesClick(OnButtonClick listener) {
        this.onYeaClick = listener;
        return this;
    }
    public interface OnButtonClick {
        void onClick();
    }
}
