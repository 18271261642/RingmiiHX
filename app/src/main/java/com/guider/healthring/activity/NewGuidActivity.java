package com.guider.healthring.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.guider.health.common.core.LocaleUtil;
import com.guider.healthring.R;
import com.guider.healthring.adpter.HomeAdapter;
import com.guider.healthring.siswatch.WatchBaseActivity;
import com.guider.healthring.util.SharedPreferencesUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎页
 */
public class NewGuidActivity extends WatchBaseActivity {


    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = findViewById(R.id.viewPager);
        initData();
    }

    private void initData() {
        //是否是第一次进入
        SharedPreferencesUtils.setParam(NewGuidActivity.this,"isGuide",true);
        List<View> pageList = createPageList();
        HomeAdapter adapter = new HomeAdapter();
        adapter.setData(pageList);
        viewPager.setAdapter(adapter);
    }

//    private final int[] imageIds = {R.drawable.ic_guid_1, R.drawable.ic_guid_2
//            ,R.drawable.ic_guid_3};
    private final int[] imageIds = {R.mipmap.image_guide_one, R.mipmap.image_guide_two
            ,R.mipmap.image_guide_three};
    private final int[] imageEnIds = {R.mipmap.image_guide_one_en, R.mipmap.image_guide_two_en
            ,R.mipmap.image_guide_three_en};

    @NonNull
    private List<View> createPageList() {
        List<View> pageList = new ArrayList<>();
        //去掉直接用mipmap.image,用添加ImageView方法，防止图片太大要求的内存太多，超出限制出现OOM
        //获取屏幕的默认分辨率
        Display display = getWindowManager().getDefaultDisplay();
        boolean isZh = LocaleUtil.isZh();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setMinimumHeight(display.getHeight());
            image.setMinimumWidth(display.getWidth());
            image.setMaxHeight(display.getHeight());
            image.setMaxHeight(display.getWidth());
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;

            InputStream is = getResources().openRawResource(
                    isZh ? imageIds[i] : imageEnIds[i] );
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
            image.setBackgroundDrawable(bd);
            pageList.add(createPageViews(image, i));
        }
        return pageList;
    }


    private View createPageViews(ImageView drawable, final int index) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.banner_guide, null,false);
        RelativeLayout activity_guide = (RelativeLayout) contentView.findViewById(R.id.activity_guide);
        if (index == 2) {
            activity_guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NewGuidActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
        activity_guide.addView(drawable);
        return contentView;
    }

}
