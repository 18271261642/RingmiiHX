package com.guider.health.bp.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TipTitleView extends LinearLayout {

    ArrayList<TextView> tvList;

    public TipTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        tvList = new ArrayList<>();

    }

    public void setTips(String... strings) {
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            TextView tipNameView = getTipNameView(str);
            TextView nextView = getNextView();
            this.addView(tipNameView);
            tvList.add(tipNameView);
            if (i + 1 >= strings.length) {
                return;
            }
            this.addView(nextView);
        }
    }

    public void toTip(int tipNumber) {
        if (tipNumber >= tvList.size()) {
            return;
        }
        for (TextView textView : tvList) {
            textView.setTextColor(Color.parseColor("#ACACAC"));
        }
        tvList.get(tipNumber).setTextColor(Color.parseColor("#F18937"));
    }

    TextView getTipNameView(String name) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 0, 0);
        textView.setText(name);
        textView.setLayoutParams(params);
        textView.setTextSize(15);
        textView.setTextColor(Color.parseColor("#ACACAC"));
        return textView;
    }

    TextView getNextView() {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 0, 0);
        textView.setLayoutParams(params);
        textView.setTextSize(15);
        textView.setTextColor(Color.parseColor("#ACACAC"));
        textView.setText(">");
        return textView;
    }
}
