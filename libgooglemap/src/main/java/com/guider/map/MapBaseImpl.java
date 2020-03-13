package com.guider.map;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.guider.libbase.map.IMapBase;

public class MapBaseImpl implements IMapBase {
    protected Activity mContext;
    protected MapView mMapView;
    protected GoogleMap mGoogleMap;

    @Override
    public void init(Activity context, View view) {
        mContext = context;
        mMapView = (MapView) view;
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                UiSettings uiSettings = mGoogleMap.getUiSettings();
                // uiSettings.l(-70);// 隐藏logo
                uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
                onMapRead();
            }
        });
    }

    protected void onMapRead() {

    }

    @Override
    public void onResume() {
        if (mMapView != null)
            mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onPause() {
        if (mMapView != null)
            mMapView.onPause();
    }

}
