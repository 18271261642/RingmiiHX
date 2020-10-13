package com.guider.healthring.imagepicker;

import android.content.Intent;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mickael on 10/10/2016.
 */

public class CameraPickerManager extends PickerManager {

    public CameraPickerManager(AppCompatActivity activity) {
        super(activity);
    }

    protected void sendToExternalApp()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mProcessingPhotoUri =  getImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
}
