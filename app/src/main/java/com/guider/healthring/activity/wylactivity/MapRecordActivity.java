package com.guider.healthring.activity.wylactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guider.healthring.R;
import com.guider.healthring.base.BaseActivity;
import com.guider.healthring.bean.B30LatlonBean;
import com.guider.libbase.map.IMapRecord;
import com.guider.libbase.map.IMapRecordView;
import com.guider.map.MapRecordImpl;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by admin on 2017/3/30.
 */

public class MapRecordActivity extends BaseActivity implements IMapRecordView,View.OnClickListener {

    private static final String TAG = "MapRecordActivity";

    TextView Starttime;
    /**
     * 开始时间
     */
    TextView Kongqi;
    /**
     * 空气质量
     */
    ImageView TianqiImage;
    /**
     * 天气图片
     */
    TextView Wendu;
    /**
     * 温度
     */
    Chronometer Duration;
    /**
     * 总计时间
     */
    TextView Fullkilometer;
    /**
     * 总公里
     */
    TextView Pace;
    /**
     * 配速
     */
    TextView Consume;
    /**
     * 消耗
     */
    LinearLayout linearLayoutONE;
    /**
     * p25视图
     */
    LinearLayout linearLayoutTwo;
    /**
     * 温度视图
     */
    RelativeLayout linearLayoutThere;
    /**
     * 温度视图
     */
    LinearLayout Back;
    /**
     * 返回
     */
    ImageView fugaiwu;

    boolean isoneline = false;//是不是第一次划线
    TextView BIAOTI;

    private IMapRecord mIMapRecord;
    List<B30LatlonBean> b30LatlonBeanList;

    @Override
    protected void initViews() {
        initViewIds();
    }

    private void initViewIds(){
        Starttime =findViewById(R.id.huwaiqixing_years);
        Kongqi =findViewById(R.id.qichekong_qizhiliangyy);
        TianqiImage =findViewById(R.id.qiche_kongqiyu);
        Wendu =findViewById(R.id.qiche_wendu);
        Duration =findViewById(R.id.test_chronometer_times);
        Fullkilometer =findViewById(R.id.test_full_kilometer);
        Pace =findViewById(R.id.test_peisu);
        Consume =findViewById(R.id.test_xiaohao_kclal);
        linearLayoutONE =findViewById(R.id.qiche_mypmyy);
        linearLayoutTwo =findViewById(R.id.qichemypm_www);
        linearLayoutThere =findViewById(R.id.test_huwaiqixing_ditut);
        Back =findViewById(R.id.qixingshu_waipao_bustar);
        fugaiwu =findViewById(R.id.qixingfugai_hostory);
        BIAOTI =findViewById(R.id.shuwaipao_buhuwai);
        Back.setOnClickListener(this);
        findViewById(R.id.huwaiq_ixingbubao_fengxiang).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.qixingshu_waipao_bustar:
                finish();
                break;
            case R.id.huwaiq_ixingbubao_fengxiang:
                mIMapRecord.screenshot(fugaiwu);
                break;
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_maprecord;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linearLayoutONE.getBackground().setAlpha(120);
        linearLayoutTwo.getBackground().setAlpha(120);
        linearLayoutThere.getBackground().setAlpha(120);


        //取得从上一个Activity当中传递过来的Intent对象
        Intent _intent = getIntent();
        //从Intent当中根据key取得value
        if (_intent != null) {
            try {
                String value = _intent.getStringExtra("mapdata");//这里是经纬度
                Log.e(TAG, "----------value=" + value + "\n" + new Gson().toJson(value));
                String value2 = _intent.getStringExtra("mapdata2");//这里是其他数据
                JSONObject JSONO = new JSONObject(value2);
                Starttime.setText(JSONO.optString("day").toString());
                if ("良".equals(JSONO.optString("description").trim())) {
                    Kongqi.setText(getResources().getString(R.string.good));
                } else if ("轻度污染".equals(JSONO.optString("description").trim())) {
                    Kongqi.setText(getResources().getString(R.string.mild_pollution));
                } else if ("中度污染".equals(JSONO.optString("description").trim())) {
                    Kongqi.setText(getResources().getString(R.string.moderate_pollution));
                } else if ("重度污染".equals(JSONO.optString("description").trim())) {
                    Kongqi.setText(getResources().getString(R.string.heavy_pollution));
                } else if ("严重污染".equals(JSONO.optString("description").trim())) {
                    Kongqi.setText(getResources().getString(R.string.serious_pollution));
                }
                Wendu.setText(JSONO.optString("temp"));
                BIAOTI.setText(JSONO.optString("qixing"));
                Duration.setText(JSONO.optString("chixutime"));
                Fullkilometer.setText(JSONO.optString("zonggongli"));
                Pace.setText(getResources().getString(R.string.paces) + JSONO.optString("speed"));
                Consume.setText(getResources().getString(R.string.XIAOHAO) + JSONO.optString("kclal"));
                System.out.print("inmage" + JSONO.optString("image"));
                BitmapUtils bitmapUtils = new BitmapUtils(MapRecordActivity.this);
                BitmapDisplayConfig config = new BitmapDisplayConfig();
                // 设置图片的分辨率
                BitmapSize size = new BitmapSize(500, 500);
                config.setBitmapMaxSize(size);
                bitmapUtils.display(TianqiImage, JSONO.optString("image"));


                //解析地图轨迹
                b30LatlonBeanList = new Gson().fromJson(value, new TypeToken<List<B30LatlonBean>>() {
                }.getType());
                com.alibaba.fastjson.JSONArray Mapdata = com.alibaba.fastjson.JSONArray.parseArray(value);
                JSONArray Mapdataa = new JSONArray(value);

                Log.d("========A===-", b30LatlonBeanList.toString());
                Log.d("========B===-", Mapdata.toString());
                Log.d("=======C====-", Mapdataa.toString());

            } catch (Exception E) {
                E.printStackTrace();
            }

            mIMapRecord = new MapRecordImpl();
            mIMapRecord.setIMapRecordView(this);
            mIMapRecord.init(this, findViewById(R.id.map));
        }
    }

    @Override
    public void drawMap() {
        try {
            int cnt = b30LatlonBeanList.size();
            for (int i = 0; i < b30LatlonBeanList.size(); i++) {
                double lng = b30LatlonBeanList.get(i).getLon();//纬度
                double lat = b30LatlonBeanList.get(i).getLat();//经度
                // Log.d("===========-", rtc + "===" + jindu);
                mIMapRecord.drawOneRecord(i, cnt, lng, lat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIMapRecord.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIMapRecord.onDestroy();
    }
}

