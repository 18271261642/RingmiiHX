package com.guider.libbase.map;

import android.widget.ImageView;

/**
 * 历史记录
 */
public interface IMapRecord extends IMapBase {
    void setIMapRecordView(IMapRecordView iMapRecordView);
    void drawOneRecord(int index, int size, double lng, double lat);
    void screenshot(ImageView imageView);
}
