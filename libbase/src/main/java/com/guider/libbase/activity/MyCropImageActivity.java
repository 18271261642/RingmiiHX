package com.guider.libbase.activity;

import android.os.Bundle;
import android.view.Menu;

import com.guider.libbase.R;
import com.theartofdev.edmodo.cropper.CropImageActivity;

/**
 * 图片裁剪Activity
 */
public class MyCropImageActivity extends CropImageActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean ret = super.onCreateOptionsMenu(menu);

        menu.findItem(R.id.crop_image_menu_crop).setTitle("裁剪");
        return ret;
    }
}
