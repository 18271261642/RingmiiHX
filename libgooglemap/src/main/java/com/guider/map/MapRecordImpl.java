package com.guider.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.guider.libbase.map.Common;
import com.guider.libbase.map.IMapRecord;
import com.guider.libbase.map.IMapRecordView;
import com.guider.libbase.map.ScreenShot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapRecordImpl extends MapBaseImpl implements IMapRecord {
    private ImageView mImageView;

    private LatLng newPoint;
    private LatLng oldPoint;

    private IMapRecordView mIMapRecordView;
    @Override
    public void setIMapRecordView(IMapRecordView iMapRecordView) {
        mIMapRecordView = iMapRecordView;
    }

    @Override
    protected void onMapRead() {
        if (mIMapRecordView != null)
            mIMapRecordView.drawMap();
    }

    @Override
    public void drawOneRecord(int index, int size, double lng, double lat) {
        LatLng currPoint = new LatLng(lat, lng);
        if (index == 0) {
            //修改地图的中心点位置
            CameraPosition cpNew = CameraPosition.fromLatLngZoom(currPoint, 15);
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
            mGoogleMap.moveCamera(cu);

            oldPoint = currPoint;
            newPoint = oldPoint;
            drawPoint(1, currPoint);
        }
        if (index == size - 1) {
            drawPoint(0, currPoint);
        }
        newPoint = currPoint;
        if (oldPoint.latitude != 90.0) {
            mGoogleMap.addPolyline((new PolylineOptions()).add(oldPoint, newPoint).color(Color.RED).width(10f).geodesic(true));
        }
        oldPoint = newPoint;
    }

    //画高德地图的起始点
    public void drawPoint(int id, LatLng latLng) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);
        mo.draggable(true);
        // mo.setFlat(false);
        // mo.autoOverturnInfoWindow(false);
        mo.zIndex(5f);
        // mo.perspective(true);
        // mo.period(1);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                id == 1 ? R.drawable.ic_start_pistion : R.drawable.ic_end_pistion);
        mo.icon(BitmapDescriptorFactory.fromBitmap(setImgSize(bitmap, 50, 60)));
        mGoogleMap.addMarker(mo);
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
    public void screenshot(ImageView imageView) {
        mImageView = imageView;
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "SDSDSdd.png");
                    boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (ifSuccess) {
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageBitmap(bitmap);
                        Date timedf = new Date();
                        SimpleDateFormat formatdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String xXXXdf = formatdf.format(timedf);
                        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + xXXXdf + ".png";
                        ScreenShot.shoot(mContext, new File(filePath));
                        Common.showShare(mContext, null, false, filePath);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mGoogleMap.snapshot(callback);
    }
}
