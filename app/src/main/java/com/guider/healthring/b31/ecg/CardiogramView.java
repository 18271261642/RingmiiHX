package com.guider.healthring.b31.ecg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2021/5/13
 */
public class CardiogramView extends View {

    //横向 60 * 5 个格子 每个格子 0.04s 绘制120s
    private int cardiogramTime = 12000;//心电图的时间  毫秒
    private int gridSize = 14;//水平大格子数量
    private int gridHeight = 15;//格子的高度

    private float mx = 10; //心电图 x轴的偏移 //移动速度
    private int mxSize = 10; //心电图 绘制多少个格子

    //计算需要绘制宽度
    private int horizontalSize = gridSize * 5 + 1; //水平线的个数
    private int verticalSize = getVerticalSize(cardiogramTime);//垂直线的个数

    private int gridColor =  Color.parseColor("#66333333");//格子的颜色
    private int cardiogramColor = Color.GREEN;//波形图颜色

    private Paint gridPaint;//画笔

    //绘制表格的画笔
    private Paint linPaint;


    List<Integer> sourceList = new ArrayList<>();



    ArrayList<Float> sourceList2 = new ArrayList<>();

    public CardiogramView(Context context) {
        super(context);
        initPaint();
    }

    public CardiogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CardiogramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(verticalSize * gridHeight
                ,horizontalSize * gridHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawCardiogram(canvas);
    }

    /**
     * 绘制格子
     */
    private void drawGrid(Canvas canvas){

        float gridWidth = (verticalSize - 1) * gridHeight;
        DashPathEffect dashPathEffect1 = new DashPathEffect(new float[]{1f, 0f}, 0);
        DashPathEffect dashPathEffect2 =  new DashPathEffect(new float[]{1f,1f},0);
        Path path = new Path();

        //横向的线条
        for (int i = 0; i < horizontalSize; i++) {
            path.reset();
            path.moveTo(0, i * gridHeight);
            path.lineTo(gridWidth, i * gridHeight);
            if (i == 0 || i % 5 == 0){
                //实线
                linPaint.setPathEffect(dashPathEffect1);
            }else {
                //虚线
                linPaint.setPathEffect(dashPathEffect2);
            }
            canvas.drawPath(path,linPaint);
        }

        //竖排的线
        for (int i = 0; i < verticalSize; i++) {
            path.reset();
            path.moveTo(i * gridHeight, 0);
            path.lineTo(i * gridHeight, (horizontalSize - 1) * gridHeight);
            if (i == 0 || i % 5 == 0){
                //实线
                linPaint.setPathEffect(dashPathEffect1);
            }else {
                //虚线
                linPaint.setPathEffect(dashPathEffect2);
            }
            canvas.drawPath(path,linPaint);
        }
        path.close();
    }

    /**
     * 绘制心电图
     */
    private void drawCardiogram(Canvas canvas){
        canvas.translate(0,getHeight()/2);
        canvas.save();
        resetPaint();
        Path path = new Path();


        if (!sourceList.isEmpty()){
            //线绘制第一条波形  绘制在3和4条格子中
//            ArrayList<Float> pqs = sourceList;//initList(MDC_ECG_LEAD_I, gridHeight * 5);
            List<Integer> pqs = new ArrayList<>();
            pqs.addAll(sourceList);

            int LEAD_I_x_0 = gridHeight  * 5;//x轴的0
            int LEAD_I_y_0 = gridHeight  * 5;//y轴的0
//            drawCardiogram("Ⅰ",pqs,canvas,LEAD_I_x_0,LEAD_I_y_0,path);
            drawCardiogram("",pqs,canvas,LEAD_I_x_0,LEAD_I_y_0,path);
        }

        path.close();
    }

    /**
     * 绘制波形
     * @param pqs
     * @param canvas
     * @param x
     * @param y
     * @param path
     */
    private void drawCardiogram(String title, List<Integer> pqs, Canvas canvas
            , float x, float y, Path path){

        //最大值
        int maxValue = Collections.max(pqs);

        //系数
        float modulsV =  getHeight() / maxValue;
        modulsV = modulsV * 0.6f;

        //绘制文字
//        canvas.drawText(title,x,y - 3 * gridHeight,gridPaint);
        //绘制波形
        for (int i = 0; i < pqs.size(); i++) {
            if (i == pqs.size() - 1) {
                break; //最后一点不绘制
            }
            path.reset();
            path.moveTo(x + (mx * i), y - pqs.get(i) );
            path.lineTo( x + (mx * (i + 1)),y - pqs.get(i + 1) );
            canvas.drawPath(path,gridPaint);
        }
    }

    private void drawCardiogram(String title, int[] pqs, Canvas canvas
            , float x, float y, Path path){
        //绘制文字
        canvas.drawText(title,x,y - 3 * gridHeight,gridPaint);
        //绘制波形
        for (int i = 0; i < pqs.length; i++) {
            if (i == pqs.length - 1) {
                break; //最后一点不绘制
            }
            path.reset();
            path.moveTo(x + (mx * i), y - pqs[i]);
            path.lineTo( x + (mx * (i + 1)),y - pqs[i+1]);
            canvas.drawPath(path,gridPaint);
        }
    }

    /**
     * 画笔初始化
     */
    private void initPaint(){
        //表格线画笔
        linPaint = new Paint();
        linPaint.setStyle(Paint.Style.STROKE);
        linPaint.setStrokeWidth(1.0f);
        linPaint.setColor(gridColor);
        linPaint.setAntiAlias(true);


    }
    private void resetPaint(){
        //绘制心电图
        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(4.0f);
        gridPaint.setColor(cardiogramColor);
        gridPaint.setAntiAlias(true);
    }

    /**
     * 计算每一个点的位置
     * @param str 需要转换的字符串
     * @param bigGridHeight 大格子的高度
     * @return
     */
    private ArrayList<Float> initList(String str, float bigGridHeight){
        //取中间段的 每段13个大格子
        int size = (int) Math.ceil((mxSize * 5 * gridHeight) / mx);
        int rmSize = (int) Math.ceil((16 * 5 * gridHeight) / mx);
        String[] strings = str.split("\t|\r|\n| ");
        ArrayList<Float> pqs = new ArrayList<>();
        for (int i = rmSize; i < strings.length; i++) {
            float pq = (Integer.valueOf(strings[i]) * bigGridHeight) / 200 ;
            pqs.add(pq);
            if (i >  rmSize + size){//5000条数据取其中的一部分
                return pqs;
            }
        }
        return pqs;
    }

    private ArrayList<Float> initList(ArrayList<Float> arrayList, float bigGridHeight){
        //取中间段的 每段13个大格子
        int size = (int) Math.ceil((mxSize * 5 * gridHeight) / mx);
        int rmSize = (int) Math.ceil((16 * 5 * gridHeight) / mx);
//        String[] strings = str.split("\t|\r|\n| ");
        ArrayList<Float> pqs = new ArrayList<>();
        for (int i = rmSize; i < arrayList.size(); i++) {
            float pq = (arrayList.get(i) * bigGridHeight) / 200 ;
            pqs.add(pq);
            if (i >  rmSize + size){//5000条数据取其中的一部分
                return pqs;
            }
        }
        return pqs;
    }

    /**
     * 补齐5个   40毫秒一个格子
     * @param cardiogramTime  //需要绘制的时间
     * @return
     */
    private int getVerticalSize(int cardiogramTime){
        int verticalSize = (int) Math.ceil(cardiogramTime / 40);
        return 5 - verticalSize % 5 + verticalSize + 1;
    }


    public List<Integer> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Integer> sourceLists) {
        sourceList.clear();
        this.sourceList.addAll(sourceLists);
        invalidate();

    }


    public ArrayList<Float> getSourceList2() {
        return sourceList2;
    }

    public void setSourceList2(ArrayList<Float> sourceList2s) {
        sourceList2.clear();
        this.sourceList2.addAll(sourceList2s);
        invalidate();
    }
}
