package com.guider.health.bp.view;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 测量结果页
 */
public class LevelViewUtil {

    ArrayList<ImageView> imgViews = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();

    public void init(ViewGroup layout, String[] names, String[] colors) {
        int childCount = layout.getChildCount();
        Log.i("aaaaaaaa", childCount + "");
        for (int i = 0; i < childCount; i++) {
            LinearLayout l = (LinearLayout) layout.getChildAt(i);
            for (int i1 = 0; i1 < l.getChildCount(); i1++) {
                View view = l.getChildAt(i1);
                Log.i("aaaaaaaa", view.getClass().getName() + " == " + (view instanceof ImageView));
                if (view instanceof ImageView) {
                    view.setVisibility(View.INVISIBLE);
                    imgViews.add((ImageView) view);
                }
                if (view instanceof TextView) {
                    TextView t = (TextView) view;
                    textViews.add(t);
                }
            }
        }
        // 设置文字和背景色
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setText(names[i]);
            textViews.get(i).setBackgroundColor(Color.parseColor(colors[i]));
        }
    }

    public void setLevel(int position) {
        for (int i = 0; i < imgViews.size(); i++) {
            imgViews.get(i).setVisibility(View.INVISIBLE);
        }
        imgViews.get(position).setVisibility(View.VISIBLE);
    }
}
