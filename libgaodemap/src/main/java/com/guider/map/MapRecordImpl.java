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
import com.guider.libbase.map.IMapRecord;
import com.guider.libbase.map.IMapRecordView;
import com.guider.libbase.map.ScreenShot;
import com.guider.libgaodemap.R;

import java.io.File;
import java.io.FileNotFoundException;
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
    protected void onMapReady() {
        mIMapRecordView.drawMap();
    }

    @Override
    public void drawOneRecord(int index, int size, double lng, double lat) {
        LatLng currPoint = new LatLng(lat, lng);
        if (index == 0) {
            //修改地图的中心点位置
            CameraPosition cp = mAMap.getCameraPosition();
            CameraPosition cpNew = CameraPosition.fromLatLngZoom(currPoint, cp.zoom);
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
            mAMap.moveCamera(cu);
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            oldPoint = currPoint;
            newPoint = oldPoint;
            drawPoint(1, currPoint);
        }
        if (index == size - 1) {
            drawPoint(0, currPoint);
        }
        newPoint = currPoint;
        if (oldPoint.latitude != 90.0) {
            mAMap.addPolyline((new PolylineOptions()).add(oldPoint, newPoint).color(Color.RED).width(10f).geodesic(true));
        }
        oldPoint = newPoint;
    }

    //画高德地图的起始点
    public void drawPoint(int id, LatLng latLng) {
        com.amap.api.maps.model.MarkerOptions mo = new com.amap.api.maps.model.MarkerOptions();
        mo.position(latLng);
        mo.draggable(true);
        mo.setFlat(false);
        mo.autoOverturnInfoWindow(false);
        mo.zIndex(5f);
        mo.perspective(true);
        mo.period(1);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), id == 1 ? R.drawable.ic_start_pistion : R.drawable.ic_end_pistion);
        mo.icon(com.amap.api.maps.model.BitmapDescriptorFactory.fromBitmap(setImgSize(bitmap, 50, 60)));
        mAMap.addMarker(mo);
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
        mAMap.getMapScreenShot(new Gaode()); /**高德地图截屏*/
    }

    public class Gaode implements AMap.OnMapScreenShotListener {
        @Override
        public void onMapScreenShot(Bitmap bitmap) {
            try {
                // 保存在SD卡根目录下，图片为png格式。
                FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/" + "eeed.png");
                boolean ifSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMapScreenShot(Bitmap bitmap, int i) {
        }
    }

}
