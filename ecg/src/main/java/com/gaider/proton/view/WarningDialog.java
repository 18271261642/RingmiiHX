package com.gaider.proton.view;

import android.content.Context;
import android.view.View;

import com.flyco.dialog.widget.base.BaseDialog;
import com.guider.health.ecg.R;

/**
 * 上传数据界面
 */
public class WarningDialog extends BaseDialog {

    public WarningDialog(Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    public View onCreateView() {
        return View.inflate(getContext() , R.layout.dialog_warning , null);
    }

    @Override
    public void setUiBeforShow() {

    }

}
