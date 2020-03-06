package com.guider.libbase.map;

import android.view.View;
import android.widget.ImageView;

/**
 * 骑自行车
 */
public interface IMapRide {
    void init(View view);

    void drawOneRecord(int index, int size, double lng, double lat);
    void screenshot(ImageView imageView);

    void onResume();
    void onDestroy();
}
