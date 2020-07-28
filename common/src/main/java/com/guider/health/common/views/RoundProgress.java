package com.guider.health.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.guider.health.common.R;


/**
 * 圆形的进度条
 * Created by luziqi on 2017/12/20.
 */
public class RoundProgress extends View{

    private Paint mPaint;
    private RectF mRectF;

    private int startAngle , endAngle;
    private boolean hasDot;
    private int progressColor , backClolor;
    private int mProgressAngle = 0;
    private int mDotSize = 10;
    private int progressWith;
    private int backProgressWidth;

    public RoundProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mRectF = new RectF();
        setStartAngle(270);
        setEndAngle(360);
        setHasDot(false);
        setProgressWith(3);
        setBackProgressWidth(1);
        setProgressColor(R.color.color_ffffff);
        setBackClolor(R.color.color_ffffff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(getProgressWith());

        float r = getWidth() - mDotSize / 2;
        // 绘制背景颜色
        int dotSize = 0;
        if (hasDot) {
            dotSize = mDotSize / 2;
        }
        mPaint.setColor(getBackClolor());
        mPaint.setStrokeWidth(backProgressWidth);
        mRectF.set(
                getProgressWith() / 2 + dotSize ,
                getProgressWith() / 2 + dotSize ,
                r + getProgressWith() / 2 - dotSize ,
                r + getProgressWith() / 2 - dotSize);
        canvas.drawArc(mRectF, getStartAngle() , getEndAngle(), false, mPaint);

        // 绘制进度
        mPaint.setColor(getProgressColor());
        mPaint.setStrokeWidth(progressWith);
        mRectF.set(
                getProgressWith() / 2 + dotSize ,
                getProgressWith() / 2 + dotSize ,
                r + getProgressWith() / 2 - dotSize,
                r + getProgressWith() / 2 - dotSize);
        canvas.drawArc(mRectF, getStartAngle() , getProgressAngle(), false, mPaint);

        // 绘制进度点
        if (hasDot) {
            mPaint.setStyle(Paint.Style.FILL);
            int z = getWidth() / 2;
            int x = (int) (z + (z - mDotSize) * Math.cos((getStartAngle() + getProgressAngle()) * 3.14f / 180));
            int y = (int) (z + (z - mDotSize) * Math.sin((getStartAngle() + getProgressAngle()) * 3.14f / 180));
            canvas.drawCircle(x , y , mDotSize , mPaint);
        }
    }

    /**
     * 设置进度
     * 0f - 1f
     * @param progress
     */
    public void setProgress(float progress) {
        progress = progress <= 0 ? 0 : progress;
        progress = progress >= 1 ? 1 : progress;
        this.mProgressAngle = (int) ((endAngle) * progress);
        invalidate();
    }

    /**
     * 设置开始角度
     */
    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    /**
     * 设置结束角度
     * 这个角度是从开始向后多少度
     * @param endAngle
     */
    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public int getEndAngle() {
        return endAngle;
    }

    public boolean isHasDot() {
        return hasDot;
    }

    public void setHasDot(boolean hasDot) {
        this.hasDot = hasDot;
    }

    public int getProgressColor() {
        try {
            return getResources().getColor(progressColor);
        } catch (Resources.NotFoundException ex) {
            return progressColor;
        }

    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getBackClolor() {
        return getResources().getColor(backClolor);
    }

    public void setBackClolor(int backClolor) {
        this.backClolor = backClolor;
    }

    public int getProgressWith() {
        return progressWith;
    }

    public void setProgressWith(int progressWith) {
        this.progressWith = progressWith;
    }

    public void setBackProgressWidth(int progressWith){
        this.backProgressWidth = progressWith;
    }

    public int getBackProgressWidth() {
        return backProgressWidth;
    }

    public int getProgressAngle() {
        return mProgressAngle;
    }

}
