package cn.wuweikang.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.guider.health.common.core.MyUtils;
import com.guider.health.common.device.DeviceInit;
import com.guider.health.common.utils.SkipClick;

import cn.wuweikang.R;


/**
 * Created by haix on 2019/7/29.
 */

public class VideoShow extends Dao12Interface {


    private VideoView mVideoView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {

            if (mVideoView != null){
                mVideoView.setVisibility(View.VISIBLE);
                if (mVideoView.isPlaying()){
                    mVideoView.resume();
                }else{
                    mVideoView.start();
                }

            }
        }else{

            if (mVideoView != null){
                mVideoView.stopPlayback();
                mVideoView.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String uri = "android.resource://" + _mActivity.getPackageName() + "/" + R.raw.dao;
        mVideoView =  view.findViewById(R.id.video_view);
        mVideoView.setVideoPath(uri);
        mVideoView.start();

        view.findViewById(R.id.skip).setVisibility(View.VISIBLE);
        view.findViewById(R.id.skip).setOnClickListener(new SkipClick(this , DeviceInit.DEV_ECG_12));

        view.findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.isNormalClickTime();
                start(new TurnOnOperator());
            }
        });
    }


}
