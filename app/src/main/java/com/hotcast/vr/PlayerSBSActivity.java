// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hotcast.vr;

import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;

import com.hotcast.vr.pageview.ChangeModeListener;
import com.hotcast.vr.pageview.PlayerContralView;
import com.hotcast.vr.sbsplayer.VideoSurfaceView;
import com.hotcast.vr.tools.L;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;


public class PlayerSBSActivity extends BaseLanActivity implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener,
        MediaController.MediaPlayerControl {

    private MediaPlayer mMediaPlayer;
    private String mUrl;
    private int videoWidth, videoHeight;
    private VideoSurfaceView mVideoSurfaceView;
//    private MediaDetailBean bean;
    private Timer playDurationTimer_;
    private PlayerContralView mPlayerContralView1,mPlayerContralView2;
    private PlayerCtrMnger mPlayerContralView;
    private int totalDuration;
    private boolean initSpliteScreen;

    @InjectView(R.id.container)
    RelativeLayout container;
    @InjectView(R.id.controller1)
    RelativeLayout controller1;
    @InjectView(R.id.controller2)
    RelativeLayout controller2;
    @InjectView(R.id.linCtr)
    View linCtr;

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void onBufferingUpdate(MediaPlayer mediaplayer, int i) {
    }

    public void onCompletion(MediaPlayer mediaplayer) {
    }

    private void initPlayer() {
        showLoading("正在加载...");
        try {
//            mMediaController = new MediaController(this);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(this.mUrl);
            L.e("URLPath", this.mUrl);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(3);
            mMediaPlayer.getVideoHeight();
            mMediaPlayer.getVideoWidth();
            mMediaPlayer.prepare();
            this.mVideoSurfaceView = new VideoSurfaceView(this, mMediaPlayer);
            mVideoSurfaceView.setSpliteScreen(initSpliteScreen);
            container.addView(this.mVideoSurfaceView);
            startPlayDurationTimer();

            mPlayerContralView1 = new PlayerContralView(this, PlayerContralView.TYPE_IMAX);
            mPlayerContralView2 = new PlayerContralView(this, PlayerContralView.TYPE_IMAX);
            mPlayerContralView = new PlayerCtrMnger(mPlayerContralView1, mPlayerContralView2);
            setPlayerContralView(linCtr);
            controller1.addView(mPlayerContralView1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            controller2.addView(mPlayerContralView2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if(initSpliteScreen){
                controller2.setVisibility(View.VISIBLE);
            }else{
                controller2.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mVideoSurfaceView != null){
            mVideoSurfaceView.onPause();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        L.e("SBS", "onDestroy");
        stopPlayDurationTimer();
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_player_sbs;
    }

    @Override
    public void init() {
        initPlayer();
    }


    @Override
    public void getIntentData(Intent intent) {
        mUrl = intent.getStringExtra("url");
        initSpliteScreen = intent.getBooleanExtra("splite_screen", false);
    }

    public void onPrepared(MediaPlayer mediaplayer) {
//        mMediaController.setMediaPlayer(this);
//        mMediaController.setAnchorView(container);
//        mMediaController.setEnabled(true);
        hideLoading();
        mMediaPlayer.seekTo(0);
        mPlayerContralView.setIsPlaying(true);
        linCtr.setVisibility(View.GONE);
        mPlayerContralView.setStatusPlay();
        mPlayerContralView.setChangeMode(new ChangeModeListener() {
            @Override
            public void clickTouchMode() {

            }

            @Override
            public void clickSplitScreen() {
                if (null != mVideoSurfaceView) {
                    mVideoSurfaceView.setSpliteScreen(!mVideoSurfaceView.isSpliteScreen());
                    if(mVideoSurfaceView.isSpliteScreen()){
                        controller2.setVisibility(View.VISIBLE);
                    }else{
                        controller2.setVisibility(View.GONE);
                    }
                }
            }
        });
        mPlayerContralView.setmPlayerContrallerInterface(new PlayerContrallerInterface() {
            @Override
            public void play() {
                if (null != mMediaPlayer) {
                    mMediaPlayer.start();
                }
            }

            @Override
            public void pause() {
                if (null != mMediaPlayer) {
                    mMediaPlayer.pause();
                }
            }

            @Override
            public void seekTo(int time) {
                if (null != mMediaPlayer) {
                    mMediaPlayer.seekTo(time * 1000);
                }
            }
        });
    }

//    @Override
//    protected void onScreenTouched() {
//        if(mPlayerContralView.isShowing()){
//            mPlayerContralView.hide();
//        }else{
//            mPlayerContralView.show();
//        }
//    }

    public void onVideoSizeChanged(MediaPlayer mediaplayer, int i, int j) {
//		L.e("video width="+i+"   video height="+j);
//		if(i == 0 || j == 0){
//			return ;
//		}
//
//		if(videoWidth != 0 || videoHeight != 0){
//			return ;
//		}
//		videoWidth = ScreenUtils.getScreenWidth(this);
//		videoHeight = (int) ((float)videoWidth*j/(float)(2*i));
//		ViewGroup.LayoutParams params = mVideoSurfaceView.getLayoutParams();
//		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//		params.height = videoHeight;
//		mVideoSurfaceView.setLayoutParams(params);
//
//		L.e("videoWidth=" + videoWidth + "   videoHeight=" + videoHeight);

    }

    public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams layoutparams) {
        super.onWindowAttributesChanged(layoutparams);
        Log.d("Test", "onAttrChg");
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    public void start() {
        hideLoading();
        mMediaPlayer.start();
    }


    /**
     * 开始播放器的播放进度检测
     */
    public void startPlayDurationTimer() {

        if (playDurationTimer_ == null) {

            playDurationTimer_ = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    if(totalDuration<=0){
                        mPlayerContralView.setMin(0);
                        totalDuration = mMediaPlayer.getDuration()/1000;
                        mPlayerContralView.setMax(totalDuration);
                        mPlayerContralView.setTotalDuration(totalDuration);
                    }
                    int curDuration = (int) mMediaPlayer.getCurrentPosition();
                    Log.e("libin", curDuration / 1000 + "|" + mMediaPlayer.getDuration() / 1000);
                    mPlayerContralView.setCurTime(curDuration / 1000);
                }
            };

            playDurationTimer_.scheduleAtFixedRate(task, 1 * 1000, 1 * 1000);
        }
    }

    /**
     * 停止播放器的播放时间进度检测
     */
    public void stopPlayDurationTimer() {

        if (playDurationTimer_ != null) {

            playDurationTimer_.cancel();
            playDurationTimer_ = null;
        }
    }

}
