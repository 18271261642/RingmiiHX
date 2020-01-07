package com.guider.healthring.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guider.healthring.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSelectView extends LinearLayout {

    @BindView(R.id.t1_img)
    ImageView t1Img;
    @BindView(R.id.t1_text)
    TextView t1Text;
    @BindView(R.id.t1)
    LinearLayout t1;
    @BindView(R.id.t2_img)
    ImageView t2Img;
    @BindView(R.id.t2_text)
    TextView t2Text;
    @BindView(R.id.t2)
    LinearLayout t2;
    @BindView(R.id.t3_img)
    ImageView t3Img;
    @BindView(R.id.t3_text)
    TextView t3Text;
    @BindView(R.id.t3)
    LinearLayout t3;
    @BindView(R.id.t4_img)
    ImageView t4Img;
    @BindView(R.id.t4_text)
    TextView t4Text;
    @BindView(R.id.t4)
    LinearLayout t4;
    @BindView(R.id.t5)
    LinearLayout t5;

    LinearLayout currentSelectView;

    int[][] imgRes = {
            {R.drawable.jilu_nor , R.drawable.jilu_sel},
            {R.drawable.shuj_nor , R.drawable.shuj_sel},
            {R.drawable.pabu_nor , R.drawable.paobu_sel},
            {R.drawable.wode_nor , R.drawable.wode_sel},
    };

    // 未选中 / 选中 字体颜色
    String[] textColor = {"#000000","#f18937"};

    public BottomSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_bottom, this);
        ButterKnife.bind(this);
        selector(t1);
    }

    private void selector(LinearLayout selectorView) {
        if (currentSelectView != null) {
            changeStatus(false , currentSelectView);
        }
        currentSelectView = selectorView;
        changeStatus(true , currentSelectView);
        if (lis != null) {
            lis.onTabSelected(currentSelectView.getId());
        }
    }

    private void changeStatus(boolean isSelector , LinearLayout view) {
        int tag  = Integer.valueOf((String) view.getTag());
        ImageView img = (ImageView) view.getChildAt(0);
        TextView text = (TextView) view.getChildAt(1);
        img.setImageResource(imgRes[tag][isSelector ? 1 : 0]);
        text.setTextColor(Color.parseColor(textColor[isSelector ? 1 : 0]));
    }

    @OnClick({R.id.t1, R.id.t2, R.id.t3, R.id.t4 , R.id.t5})
    public void onViewClicked(View view) {
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
