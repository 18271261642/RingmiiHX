package com.guider.glu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by haix on 2019/6/22.
 */


@SuppressLint("AppCompatCustomView")
public class GLUCheckableButton extends LinearLayout implements Checkable {


    private boolean mChecked = false;

    private String tag;

    public GLUCheckableButton(Context context) {
        super(context);
    }

    public GLUCheckableButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GLUCheckableButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return tag;
    }


    @Override
    public void setChecked(boolean checked) {

        this.mChecked = checked;
        refreshDrawableState();
    }

    public boolean changeCheckedStatus(){
        boolean b = isChecked();
        if (b){
            setChecked(false);
            b = false;
        }else{
            setChecked(true);
            b = true;
        }
        return b;
    }


    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }
}

