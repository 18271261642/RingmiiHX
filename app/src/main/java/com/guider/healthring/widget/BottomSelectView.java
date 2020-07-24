package com.guider.healthring.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guider.healthring.R;

public class BottomSelectView extends LinearLayout implements View.OnClickListener {

    ImageView t1Img;
    TextView t1Text;
    LinearLayout t1;
    ImageView t2Img;
    TextView t2Text;
    LinearLayout t2;
    ImageView t3Img;
    TextView t3Text;
    LinearLayout t3;
    ImageView t4Img;
    TextView t4Text;
    LinearLayout t4;
    LinearLayout t5;

    LinearLayout currentSelectView;

    int[][] imgRes = {
            {R.drawable.jilu_nor, R.drawable.jilu_sel},
            {R.drawable.shuj_nor, R.drawable.shuj_sel},
            {R.drawable.pabu_nor, R.drawable.paobu_sel},
            {R.drawable.wode_nor, R.drawable.wode_sel},
    };

    // 未选中 / 选中 字体颜色
    String[] textColor = {"#000000", "#f18937"};

    public BottomSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.view_bottom, this);
        initViewIds(view);
        selector(t1);
    }

    private void initViewIds(View view) {
        t1Img = view.findViewById(R.id.t1_img);
        t1Text = view.findViewById(R.id.t1_text);
        t1 = view.findViewById(R.id.t1);
        t2Img = view.findViewById(R.id.t2_img);
        t2Text = view.findViewById(R.id.t2_text);
        t2 = view.findViewById(R.id.t2);
        t3Img = view.findViewById(R.id.t3_img);
        t3Text = view.findViewById(R.id.t3_text);
        t3 = view.findViewById(R.id.t3);
        t4Img = view.findViewById(R.id.t4_img);
        t4 = view.findViewById(R.id.t4);
        t4Text = view.findViewById(R.id.t4_text);
        t5 = view.findViewById(R.id.t5);
        t2.setOnClickListener(this);
        t1.setOnClickListener(this);
        t3.setOnClickListener(this);
        t4.setOnClickListener(this);
        t5.setOnClickListener(this);
    }

    private void selector(LinearLayout selectorView) {
        if (currentSelectView != null) {
            changeStatus(false, currentSelectView);
        }
        currentSelectView = selectorView;
        changeStatus(true, currentSelectView);
        if (lis != null) {
            lis.onTabSelected(currentSelectView.getId());
        }
    }

    private void changeStatus(boolean isSelector, LinearLayout view) {
        int tag = Integer.valueOf((String) view.getTag());
        ImageView img = (ImageView) view.getChildAt(0);
        TextView text = (TextView) view.getChildAt(1);
        img.setImageResource(imgRes[tag][isSelector ? 1 : 0]);
        text.setTextColor(Color.parseColor(textColor[isSelector ? 1 : 0]));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.t1:
                selector(t1);
                break;
            case R.id.t2:
                selector(t2);
                break;
            case R.id.t3:
                selector(t3);
                break;
            case R.id.t4:
                selector(t4);
                break;
            case R.id.t5:
                if (lis != null) {
                    lis.onTabSelected(view.getId());
                }
                break;
        }
    }

    private OnTabSelectListener lis;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.lis = listener;
    }

    public interface OnTabSelectListener {
        void onTabSelected(int tabId);
    }

}
