package com.guider.healthring.b30.b30run;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.guider.healthring.MyApp;
import com.guider.healthring.R;
import com.guider.healthring.b30.DevicesSportHisyory;
import com.guider.healthring.b30.GPSSportHisyory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin
 * Date 2018/12/13
 */
public class B36RunFragment extends Fragment implements View.OnClickListener{


    private static final String TAG = "B36RunFragment";


    View runView;

    ViewPager fragmentViewPager;
    TextView gpsSportTv;
    TextView deviceSportTc;
    private String[] titleStr = new String[]{"GPS运动", "手环运动"};

    private List<Fragment> fragmentList = new ArrayList<>();
    private ChildViewPagerAdapter childViewPagerAdapter;
    private ChildGPSFragment childGPSFragment;
    private ChildDeviceSportFragment childDeviceSportFragment;


    private int currPage = 0;
    private Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        runView = inflater.inflate(R.layout.fragment_b36_run_layout, container, false);
        initViewIds();
        initViews();


        return runView;
    }

    private void initViewIds() {
        fragmentViewPager =runView.findViewById(R.id.fragmentViewPager);
        gpsSportTv =runView.findViewById(R.id.gpsSportTv);
        deviceSportTc =runView.findViewById(R.id.deviceSportTc);
        gpsSportTv.setOnClickListener(this);
        deviceSportTc.setOnClickListener(this);
        runView.findViewById(R.id.deviceSportHistoryImg).setOnClickListener(this);
    }

    private void initViews() {
        fragmentList.clear();
        if (childGPSFragment == null)
            childGPSFragment = new ChildGPSFragment();
        if (childDeviceSportFragment == null)
            childDeviceSportFragment = new ChildDeviceSportFragment();
        fragmentList.add(childGPSFragment);
        fragmentList.add(childDeviceSportFragment);
        childViewPagerAdapter = new ChildViewPagerAdapter(getChildFragmentManager());
        fragmentViewPager.setAdapter(childViewPagerAdapter);
        fragmentViewPager.setCurrentItem(0);
        clearStyleData(0);
        fragmentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currPage = i;
                clearStyleData(i);
                fragmentViewPager.setCurrentItem(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public Context getmContext() {
        return mContext==null?MyApp.getContext():mContext;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gpsSportTv:
                currPage = 0;
                clearStyleData(0);
                fragmentViewPager.setCurrentItem(0);
                break;
            case R.id.deviceSportTc:
                currPage = 1;
                clearStyleData(1);
                fragmentViewPager.setCurrentItem(1);
                break;
            case R.id.deviceSportHistoryImg:    //历史记录
                if (currPage == 0) {
                    startActivity(new Intent(getmContext(), GPSSportHisyory.class));
                } else {
                    startActivity(new Intent(getmContext(), DevicesSportHisyory.class));
                }
                break;
        }
    }


    private class ChildViewPagerAdapter extends FragmentPagerAdapter {


        public ChildViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//        }
//
//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            return super.isViewFromObject(view, object);
//        }
    }


    private void clearStyleData(int code){

        gpsSportTv.setTextColor(Color.parseColor("#333333"));
        gpsSportTv.setBackground(getResources().getDrawable(R.drawable.newh9data_selecte_text_shap));
        deviceSportTc.setTextColor(Color.parseColor("#333333"));
        deviceSportTc.setBackground(getResources().getDrawable(R.drawable.newh9data_selecte_text_shap));

        switch (code){
            case 0:
                gpsSportTv.setTextColor(getResources().getColor(R.color.white));
                gpsSportTv.setBackground(getResources().getDrawable(R.drawable.newh9data_unselecte_text_shap_one));
                break;
            case 1:
                deviceSportTc.setTextColor(getResources().getColor(R.color.white));
                deviceSportTc.setBackground(getResources().getDrawable(R.drawable.newh9data_unselecte_text_shap_two));
                break;


        }
    }

}
