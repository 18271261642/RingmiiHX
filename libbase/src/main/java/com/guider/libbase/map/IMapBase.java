package com.guider.libbase.map;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

/**
 * 历史记录
 */
public interface IMapBase {
    void init(Activity context, View view);

    void onResume();
    void onDestroy();
    void onPause();
}
