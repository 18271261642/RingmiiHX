package com.guider.healthring.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.guider.healthring.R;
import com.guider.healthring.util.Common;

/**
 * Created by thinkpad on 2017/3/24.
 */

public class CircleButton extends TextView {

    private Context mContext;

    Paint paint;

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(ContextCompat.getColor(mContext, R.color.colorAccent));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 3.0);
        canvas.drawCircle(Common.dip2px(mContext, 78), Common.dip2px(mContext, 78), (getMeasuredWidth() - 10) / 2, paint);
        super.onDraw(canvas);
    }
}
