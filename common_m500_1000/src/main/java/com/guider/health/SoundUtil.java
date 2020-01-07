package com.guider.health;

import android.content.Context;
import android.media.MediaPlayer;

import com.guider.health.all.R;

import java.io.IOException;


/**
 * 训练语音提示
 * Created by luziqi on 2017/12/9.
 */
public class SoundUtil {

    private MediaPlayer mBackMusic;

    private float mCurrentVolume = 1f;

    private Context mContext;

    private SoundUtil(Context context) {
        load(context);
    }

    private static SoundUtil instans;

    public static SoundUtil getInstans(Context context) {
        if (instans == null) {
            synchronized (SoundUtil.class) {
                if (instans == null) {
                    instans = new SoundUtil(context);
                }
            }
        }
        return instans;
    }

    public void load(Context context) {
        mContext = context.getApplicationContext();
        if (mBackMusic == null) {
            mBackMusic = MediaPlayer.create(mContext, R.raw.sound_welcom);
            mBackMusic.setLooping(false);
            mBackMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });
        }
    }

    public void stop() {
        if (mBackMusic.isPlaying()) {
            mBackMusic.stop();
            try {
                mBackMusic.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        stop();
        mBackMusic.start();
    }

    public void pauseBackMusic() {
        if (mBackMusic != null) {
            mBackMusic.pause();
        }
    }

}
