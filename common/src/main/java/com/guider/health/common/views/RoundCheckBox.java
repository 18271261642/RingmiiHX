package com.guider.health.common.views;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.guider.health.common.R;

public class RoundCheckBox extends AppCompatCheckBox {

    public RoundCheckBox(Context context) {
        this(context, null);
    }

    public RoundCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public RoundCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}