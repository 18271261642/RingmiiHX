package com.guider.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.guider.libbase.map.Common;
import com.guider.libbase.map.IMapBase;
import com.guider.libbase.map.IMapRecord;
import com.guider.libbase.map.ScreenShot;
import com.guider.libgaodemap.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapBaseImpl implements IMapBase {
    protected Activity mContext;
    protected AMap mAMap;
    protected MapView mMapView;

    @Override
    public void init(Activity context, View view) {
        mContext = context;
        mMapView = (MapView) view;
        mMapView.onCreate(null);
        mAMap = mMapView.getMap();
        UiSettings uiSettings = mAMap.getUiSettings();
        uiSettings.setLogoBottomMargin(-70);// 隐藏logo
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        onMapReady();
    }

    protected void onMapReady() {

    }

    @Override
    public void onResume() {
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
    }


}
