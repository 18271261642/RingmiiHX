package com.tc.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.util.List;

public class CarKeyboardView extends KeyboardView {
    private Drawable mKeyDrawable;
    private Rect rect;
    private Paint paint;

    public CarKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources(context);
    }

    public CarKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources(context);
    }

    private void initResources(Context context) {
        Resources res = context.getResources();
        rect = new Rect();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(50);
        paint.setColor(res.getColor(android.R.color.black));
    }

    public void setKeyDrawable(Drawable mKeyDrawable){
        this.mKeyDrawable = mKeyDrawable;
        invalidate();
    }

    public Paint getPaint(){
        return paint;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
        if (paint==null){
            throw new NullPointerException("Paint is not null");
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Keyboard keyboard = getKeyboard();
        if (keyboard == null) return;
        List<Keyboard.Key> keys = keyboard.getKeys();
        if (keys != null && keys.size() > 0) {
            @SuppressLint("DrawAllocation")
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paint.setTypeface(font);
            paint.setAntiAlias(true);
            for (Keyboard.Key key : keys) {
                canvas.save();

                if (key.codes[0] == -4) { // 下一步
                    Drawable dr = getContext().getResources().getDrawable(R.mipmap.key_next);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == -1) { // 上一步
                    Drawable dr = getContext().getResources().getDrawable(R.mipmap.key_last);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else if (key.codes[0] == -5) { // 删除
                    Drawable dr = getContext().getResources().getDrawable(R.mipmap.key_dele);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                }

                canvas.restore();
            }
        }
    }
}
