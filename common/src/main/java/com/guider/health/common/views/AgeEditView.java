package com.guider.health.common.views;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.guider.health.common.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 年龄输入控件
 */
public class AgeEditView extends LinearLayout {

    private EditText year, month, day;

    private int yearMin = 1800, yearMax;
    private final int monthMin = 1, monthMax = 12;
    private final int dayMin = 1, dayMax = 31;

    public static final String NULL = "NULL";
    public static final String ILLEGAL = "Illegal";

    public AgeEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_age_input, this);
        yearMax = Integer.valueOf(new SimpleDateFormat("yyyy").format(new Date()));
        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        day = findViewById(R.id.day);
        year.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    valueLimit(yearMin, yearMax, year);
                }
            }
        });
//        new KeyBoardUtil((Activity) context, year).setOnFocusChangeListener();
        month.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    valueLimit(monthMin, monthMax, month);
                }
            }
        });
//        new KeyBoardUtil((Activity) context, month).setOnFocusChangeListener();
        day.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    valueLimit(dayMin, dayMax, day);
                }
            }
        });
//        new KeyBoardUtil((Activity) context, day).setOnFocusChangeListener();
    }

    public String getValue() {
        String strYear = year.getText().toString();
        String strMonth = month.getText().toString();
        String strDay = day.getText().toString();
        if (TextUtils.isEmpty(strYear) || TextUtils.isEmpty(strMonth) || TextUtils.isEmpty(strDay)) {
            return NULL;
        }
        int y = Integer.valueOf(strYear);
        int m = Integer.valueOf(strMonth);
        int d = Integer.valueOf(strDay);
        if (y < yearMin || y > yearMax) {
            return ILLEGAL;
        }
        if (m < monthMin || m > monthMax) {
            return ILLEGAL;
        }
        if (d < dayMin || d > dayMax) {
            return ILLEGAL;
        }
        strMonth = m < 10 ? "0" + m : m + "";
        strDay = d < 10 ? "0" + d : d + "";
        return strYear + "-" + strMonth + "-" + strDay;
    }

    private void valueLimit(int min, int max, EditText editText) {
        String weight = editText.getText().toString();
        if (!TextUtils.isEmpty(weight)) {
            if (Integer.valueOf(weight) > max) {
                editText.setText(max + "");
                editText.setSelection(editText.getText().length());
            } else if (Integer.valueOf(weight) < min) {
                editText.setText(min + "");
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public void setValue(String dataTime) {
        String[] split = dataTime.split("-");
        if (split.length >= 3) {
            year.setText(split[0]);
            month.setText(split[1]);
            day.setText(split[2]);
        }
    }

    public void setNextEditText(int nextId) {
        day.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        day.setNextFocusForwardId(nextId);
    }
}
