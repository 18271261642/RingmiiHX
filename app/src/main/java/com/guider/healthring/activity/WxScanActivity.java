package com.guider.healthring.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guider.health.apilib.ApiCallBack;
import com.guider.health.apilib.ApiUtil;
import com.guider.health.apilib.ApiConsts;
import com.guider.health.apilib.IGuiderApi;
import com.guider.health.common.utils.AppUtils;
import com.guider.health.common.utils.ScreenShotUtils;
import com.guider.health.common.utils.StringUtil;
import com.guider.health.common.views.dialog.DialogProgress;
import com.guider.healthring.R;
import com.guider.healthring.siswatch.NewSearchActivity;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Response;

public class WxScanActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private DialogProgress mDialogProgress;
    private byte mNeedReload; // 0 需要加载， 1 加载中， 2 不需要加载
    private long mAccountId;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat);

        init();
    }

    private void init() {
        // 初始化
        mAccountId = getIntent().getLongExtra("accountId", 0);
        if (mAccountId <= 0) {
            toNext();
            return;
        }
        mNeedReload = 0;

        TextView tvNext = findViewById(R.id.tv_next);
        tvNext.setOnClickListener(this);

        final ImageView ivSaveScreeShot = findViewById(R.id.iv_wechat_qr);
        ivSaveScreeShot.setOnLongClickListener(this);
        ivSaveScreeShot.setOnClickListener(this);

        loadingWxQr(ivSaveScreeShot, mAccountId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                toNext();
                break;
            case R.id.iv_wechat_qr:
                if (mNeedReload != 0)
                    return;
                final ImageView ivSaveScreeShot = findViewById(R.id.iv_wechat_qr);
                loadingWxQr(ivSaveScreeShot, mAccountId);
                break;
        }
    }

    private synchronized void loadingWxQr(ImageView ivSaveScreeShot, long accountId) {
        mNeedReload = 1;
        ApiUtil.createApi(IGuiderApi.class, true)
                .createWXQr(ApiConsts.WX_APPID, 2592000, "appBind=" + accountId)
                .enqueue(new ApiCallBack<String>() {
                    @Override
                    public void onApiResponse(Call<String> call, Response<String> response) {
                        String url = response.body();
                        if (StringUtil.isEmpty(url)) {
                            mNeedReload = 0;
                            return;
                        }
                        Picasso.with(WxScanActivity.this)
                                .load(url)
                                .placeholder(R.mipmap.loading_pic)
                                .error(R.mipmap.reload)
                                .fit()
                                .into(ivSaveScreeShot);
                        mNeedReload = 2;
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        super.onFailure(call, t);
                        mNeedReload = 0;
                        ivSaveScreeShot.setImageResource(R.mipmap.reload);
                    }
                });
    }

    private void toNext() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.iv_wechat_qr:
                mDialogProgress = new DialogProgress(this, null);
                mDialogProgress.showDialog();
                if (ScreenShotUtils.shotScreen(this, "wx_scan_qr", true , 2592000L * 1000L)) {
                    Toast.makeText(this, getResources().getString(R.string.wx_scan_screen_save_ok), Toast.LENGTH_LONG).show();
                    toNext();

                    AppUtils.startWx(this);
                } else
                    Toast.makeText(this, getResources().getString(R.string.wx_scan_screen_save_error), Toast.LENGTH_LONG).show();
                mDialogProgress.hideDialog();
                break;
        }
        return false;
    }
}
