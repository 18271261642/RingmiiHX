package com.guider.libbase.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.guider.health.common.utils.StringUtil;
import com.guider.libbase.R;
import com.guider.libbase.util.AppPath;
import com.guider.libbase.util.DateUtil;
import com.guider.libbase.util.FileUtil;
import com.guider.libbase.util.ImageRotateUtil;
import com.guider.libbase.util.Md5Util;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;

/**
 * 图片选择界面
 */
public class PictureSelectActivity extends AppCompatActivity implements MultiImageSelectorFragment.Callback {
    /**
     * 剪切模式，自动，手动
     */
    public static final String EXTRA_CROP_MODE = "crop_mode";
    /**
     * 分辨率 x
     */
    public static final String EXTRA_RESOLUTION_X = "resolution_x";
    /**
     * 分辨率 y
     */
    public static final String EXTRA_RESOLUTION_Y = "resolution_y";
    /**
     * 比例 x
     */
    public static final String EXTRA_ASPECT_RADIO_X = "aspect_radio_x";
    /**
     * 比例 y
     */
    public static final String EXTRA_ASPECT_RADIO_Y = "aspect_radio_y";
    /**
     * 大小， bit, -1表示不压缩
     */
    public static final String EXTRA_CROP_SIZE = "crop_size";
    /**
     * 图片存储位置
     */
    public static final String EXTRA_SAVE_PATH = "save_path";
    /**
     * 图片压缩等状况下产生新的图片是否需要通知系统
     */
    public static final String EXTRA_SCAN_FILE = "scan_file";

    public static final int CROP_MODE_NONE = -1; // 不裁剪
    public static final int CROP_MODE_HAND = 0;
    public static final int CROP_MODE_AUTO = 1;

    private int mCropMode = CROP_MODE_HAND;
    /**
     * 分辨率
     */
    private int mResolutionX = 99999;
    private int mResolutionY = 99999;
    /**
     * 大小
     */
    private int mCropSize = 99999;
    /**
     * 比例，手工裁剪时才有的参数
     */
    private int mAspectRatioX = 1;
    private int mAspectRatioY = 1;

    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    private static final int DEFAULT_IMAGE_SIZE = 9;
    private ArrayList<String> resultList = new ArrayList();
    private Button mSubmitButton;
    private int mDefaultCount = 9;
    private int mMode = MODE_MULTI;

    private boolean mScanFile = false;

    /**
     * 图片存储位置
     */
    private String mSavePath = null;


    public PictureSelectActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.MIS_NO_ACTIONBAR);
        this.setContentView(R.layout.mis_activity_default);
        if (Build.VERSION.SDK_INT >= 21) { // -16777216
            this.getWindow().setStatusBarColor(1);
        }
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        if (toolbar != null) {
            this.setSupportActionBar(toolbar);
        }

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("图片选择");
        }

        Intent intent = this.getIntent();
        this.mDefaultCount = intent.getIntExtra("max_select_count", 9);
        mMode = intent.getIntExtra("select_count_mode", 1);
        boolean isShow = intent.getBooleanExtra("show_camera", true);
        if (mMode == MODE_MULTI && intent.hasExtra("default_list")) {
            this.resultList = intent.getStringArrayListExtra("default_list");
        }

        // 文件扫描
        mScanFile = intent.getBooleanExtra(EXTRA_SCAN_FILE, false);

        // 照片裁剪需要
        this.mCropMode = intent.getIntExtra(EXTRA_CROP_MODE, CROP_MODE_HAND);
        mCropSize = intent.getIntExtra(EXTRA_CROP_SIZE, Integer.MAX_VALUE);
        mResolutionX = intent.getIntExtra(EXTRA_RESOLUTION_X, 99999);
        mResolutionY = intent.getIntExtra(EXTRA_RESOLUTION_Y, 99999);
        mAspectRatioX = intent.getIntExtra(EXTRA_ASPECT_RADIO_X, 1);
        mAspectRatioY = intent.getIntExtra(EXTRA_ASPECT_RADIO_Y, 1);

        mSavePath = intent.getStringExtra(EXTRA_SAVE_PATH); // 存储的文件夹

        this.mSubmitButton = (Button) this.findViewById(R.id.commit);
        if (mMode == MODE_MULTI) {
            updateDoneText(this.resultList);
            this.mSubmitButton.setVisibility(View.VISIBLE);
            this.mSubmitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mSubmitButton.setEnabled(false);
                    if (PictureSelectActivity.this.resultList != null && PictureSelectActivity.this.resultList.size() > 0) {
                        if (onCrop(mCropMode)) {
                            mSubmitButton.setEnabled(true);
                            return;
                        }
                        Intent data = new Intent();
                        data.putStringArrayListExtra("select_result", PictureSelectActivity.this.resultList);
                        PictureSelectActivity.this.setResult(-1, data);
                    } else {
                        PictureSelectActivity.this.setResult(0);
                    }
                    PictureSelectActivity.this.finish();
                    mSubmitButton.setEnabled(true);
                }
            });
        } else {
            this.mSubmitButton.setVisibility(View.INVISIBLE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("max_select_count", this.mDefaultCount);
            bundle.putInt("select_count_mode", mMode);
            bundle.putBoolean("show_camera", isShow);
            bundle.putStringArrayList("default_list", this.resultList);
            this.getSupportFragmentManager().beginTransaction().add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle)).commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                this.setResult(0);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDoneText(ArrayList<String> resultList) {
        int size = 0;
        if (resultList != null && resultList.size() > 0) {
            size = resultList.size();
            this.mSubmitButton.setEnabled(true);
        } else {
            this.mSubmitButton.setText(R.string.mis_action_done);
            this.mSubmitButton.setEnabled(false);
        }

        this.mSubmitButton.setText(this.getString(R.string.mis_action_button_string, new Object[]{this.getString(R.string.mis_action_done), Integer.valueOf(size), Integer.valueOf(this.mDefaultCount)}));
    }

    public void onSingleImageSelected(String path) {
        resultList.clear();
        this.resultList.add(path);

        if (onCrop(mCropMode))
            return;

        Intent data = new Intent();
        data.putStringArrayListExtra("select_result", this.resultList);
        this.setResult(-1, data);
        this.finish();
    }

    public void onImageSelected(String path) {
        if (!this.resultList.contains(path)) {
            this.resultList.add(path);
        }

        this.updateDoneText(this.resultList);
    }

    public void onImageUnselected(String path) {
        if (this.resultList.contains(path)) {
            this.resultList.remove(path);
        }

        this.updateDoneText(this.resultList);
    }

    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            if (mScanFile)
                this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(imageFile)));
            Intent data = new Intent();
            this.resultList.add(imageFile.getAbsolutePath());

            if (onCrop(mCropMode))
                return;

            data.putStringArrayListExtra("select_result", this.resultList);
            this.setResult(-1, data);
            this.finish();
        }
    }

    protected boolean onCrop(int cropMode) {
        return onCrop(false, cropMode);
    }

    protected boolean onCrop(final boolean delFlag, int cropMode) {
        if (resultList.isEmpty())
            return false;

        if (CROP_MODE_NONE == cropMode)
            return false;

        if (MODE_MULTI == mMode && CROP_MODE_HAND == cropMode)
            return false;

        if (MODE_SINGLE == mMode && CROP_MODE_HAND == cropMode) {
            String url = resultList.get(0);
            String saveUrl = getFileName(url);
            CropImage.activity(parseFromPath(url))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputUri(parseFromPath(saveUrl))
                    // .setMaxCropResultSize(99999, 99999)
                    .setAspectRatio(mAspectRatioX, mAspectRatioY)
                    .setActivityTitle("裁剪")
                    .start(this, MyCropImageActivity.class);
            return true;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<String> tmpList = new ArrayList<String>();

                    String savePath;
                    Bitmap bitmap;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    for (String filePath : resultList) {
                        // 计算文件名
                        savePath = getFileName(filePath);
                        // 压缩
                        bitmap = getSmallBitmap(filePath);
                        baos.reset();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                        FileOutputStream fos = new FileOutputStream(new File(savePath));
                        baos.writeTo(fos);
                        baos.flush();
                        double oldDegree = ImageRotateUtil.readPictureDegree(filePath);
                        ImageRotateUtil.setPictureDegree(savePath, oldDegree);
                        // 删除老的文件
                        if (delFlag)
                            FileUtil.deleteFile(filePath);

                        if (mScanFile)
                            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", parseFromPath(savePath)));
                        tmpList.add(savePath);
                        resultList = tmpList;

                        PictureSelectActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onBack();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PictureSelectActivity.this, "图片压缩失败!", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();

        return true;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示,此处是采样率压缩
    public Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, mResolutionX, mResolutionY);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    // 计算图片的缩放值
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                if (mScanFile)
                    this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", resultUri));
                resultList.clear();
                resultList.add(resultUri.getPath());
                // onBack();
                onCrop(true, CROP_MODE_AUTO);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    protected void onBack() {
        onBack(true);
    }

    protected void onBack(boolean isFinish) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("select_result", PictureSelectActivity.this.resultList);
        PictureSelectActivity.this.setResult(-1, intent);
        if (isFinish)
            PictureSelectActivity.this.finish();
    }

    private Uri parseFromPath(String path) {
        return Uri.fromFile(new File(path));
    }

    private String getFileName(String url) {
        String path = StringUtil.isEmpty(mSavePath) ? AppPath.getPath(PictureSelectActivity.this, "/.tmp") + "/" : mSavePath;
        // String name = FileUtil.getFileName(url);
        String ex = FileUtil.getFileSuffix(url);
        return path + Md5Util.toMd5(url + DateUtil.localNowString()) + "." + ex;
    }
}
