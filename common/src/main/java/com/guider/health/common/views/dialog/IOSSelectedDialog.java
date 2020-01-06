package com.guider.health.common.views.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.base.BaseDialog;
import com.guider.health.common.R;


/**
 * Created by luziqi on 2018/2/8.
 */

public class IOSSelectedDialog extends BaseDialog {

    private View view;
    private TextView yes , no;
    private String msg , yesText , noText;
    private View.OnClickListener okClick , noClick;

    public IOSSelectedDialog(Context context , String msg) {
        super(context);
        this.msg = msg;
        setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_training_exit, null);
        TextView tvMsg = (TextView) view.findViewById(R.id.msg);
        tvMsg.setText(msg);
        yes = (TextView) view.findViewById(R.id.yes);
        no = (TextView) view.findViewById(R.id.no);
        yes.setOnClickListener(okClick);
        no.setOnClickListener(noClick);
        if (yesText != null) {
            yes.setText(yesText);
        }
        if (noText != null) {
            no.setText(noText);
        }
        return view;
    }

    public void setYesText(String yesText) {
        this.yesText = yesText;
    }

    public void setNoText(String noText) {
        this.noText = noText;
    }

    @Override
    public void setUiBeforShow() {

    }

    public IOSSelectedDialog setOkCallback(View.OnClickListener clickListener) {
        okClick = clickListener;
        return this;
    }

    public IOSSelectedDialog setNoCallback(View.OnClickListener clickListener) {
        noClick = clickListener;
        return this;
    }

}
