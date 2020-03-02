package com.guider.healthring.siswatch.run;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guider.healthring.R;
import com.guider.healthring.b30.B30BaseFragment;
import com.guider.healthring.b30.GPSSportHisyory;

import java.util.ArrayList;
import java.util.List;

public class B30RunFragmentNew extends B30BaseFragment implements
        TabLayout.OnTabSelectedListener, View.OnClickListener {

    private TabLayout mTabLayout = null;
    private int pages = 0;
    private ViewPagerSlide viewPagerSlide = null;
    private ImageView imageHis;

    /**
     * tab 选中
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pages = tab.getPosition();
        if (pages == 0) {
            imageHis.setVisibility(View.VISIBLE);
        } else {
            imageHis.setVisibility(View.GONE);
        }
//        setFragment(pages);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);

    }


    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_b30_run_layout, container, false);
    }

    @Override
    protected void initView(View root) {
        View GpsRun = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_new_run_layout, null, false);
        initGpsRunView(GpsRun);
        View DeviceRun = LayoutInflater.from(getActivity()).inflate(R.layout.b30_devices_fragment, null, false);
        initDeviceRunView(DeviceRun);
        if (imageHis == null) imageHis = root.findViewById(R.id.watch_run_sportHistoryTitleTv);
        imageHis.setOnClickListener(this);
        if (viewPagerSlide == null) viewPagerSlide = root.findViewById(R.id.run_view_pager);
        if (mTabLayout == null) mTabLayout = (TabLayout) root.findViewById(R.id.mTabLayout);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.string_gps_run)));//"GPS运动"
//        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.string_devices_run)));//"手环运动"
        mTabLayout.addOnTabSelectedListener(this);


        mTabLayout.setupWithViewPager(viewPagerSlide);


        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(W30sNewRunFragment.newInstance());
        fragmentList.add(B30DevicesFragment.newInstance());
        viewPagerSlide.setAdapter(new RunVPAdapter(getActivity().getSupportFragmentManager(), fragmentList));
    }

    /**
     * 手环运动
     *
     * @param deviceRun
     */
    private void initDeviceRunView(View deviceRun) {

    }

    /**
     * GPS运动
     *
     * @param gpsRun
     */
    private void initGpsRunView(View gpsRun) {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void lazyLoad() {
    }

    /**
     * 历史纪录图片点击
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), GPSSportHisyory.class));
    }

    public class RunVPAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = null;
        String[] stringsTitle = {getResources().getString(R.string.string_gps_run),
                getResources().getString(R.string.string_devices_run)};

        public RunVPAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }


        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringsTitle[position];
        }
    }
}
