/*
 * SimpleStreamPlayer
 * Android example of Panframe library
 * The example plays back an panoramic movie from a resource.
 * 
 * (c) 2012-2013 Mindlight. All rights reserved.
 * Visit www.panframe.com for more information. 
 * 
 */

package com.hotcast.vr;

import java.util.*;


import com.hotcast.vr.pageview.ChangeModeListener;
import com.hotcast.vr.pageview.PlayerContralView;
import com.hotcast.vr.tools.L;
import com.panframe.android.lib.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.*;

import butterknife.InjectView;

public class PlayerVRActivityNew extends BaseLanActivity implements PFAssetObserver, OnSeekBarChangeListener {

    PFView _pfview;
    PFAsset _pfasset;
    PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;
    private boolean ctr_vist = true;//控制条为显示状态
    boolean _updateThumb = true;
    Timer _scrubberMonitorTimer;

    @InjectView(R.id.framecontainer)
    ViewGroup _frameContainer;
    @InjectView(R.id.controller1)
    RelativeLayout controller1;
    @InjectView(R.id.controller2)
    RelativeLayout controller2;
    @InjectView(R.id.linCtr)
    View linCtr;


    private PlayerContralView mPlayerContralView1, mPlayerContralView2;
    private PlayerCtrMnger mPlayerContralView;
    private int totalDuration;
    public static final int MODE_SPLIT_SCREEN = 2;
    public static final int MODE_NORMAL = 0;
    private int curMode = MODE_SPLIT_SCREEN;
    private boolean isPause = false;

    private Handler mHideSystemUIHandler = new Handler() {

        public void handleMessage(Message message) {
            getWindow().getDecorView().setSystemUiVisibility(1542);
            if(null != playerContralView){
//                playerContralView.hide();
                linCtr.setVisibility(View.GONE);
            }
        }
    };
//    double nLenStart = 0;


    /**
     * Start the video with a local file path
     *
     * @param filename The file path on device storage
     */

    public void loadVideo(String filename) {
        L.e("filename=" + filename);
//        System.out.println("---播放页面接受到的url："+filename);
        _pfview = PFObjectFactory.view(this);
        _pfview.setMode(curMode, 0);

//        _pfview.setFieldOfView(150);

        _pfasset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);

        _pfview.displayAsset(_pfasset);
        _pfview.setNavigationMode(_currentNavigationMode);
//        _pfview.setFieldOfView(100);

        _frameContainer.addView(_pfview.getView(), 0);

        _pfasset.play();

        System.out.println("---104--开始加载的时间 = " + System.currentTimeMillis());
        showLoading("正在加载...");
    }

    /**
     * Status callback from the PFAsset instance.
     * Based on the status this function selects the appropriate action.
     *
     * @param asset  The asset who is calling the function
     * @param status The current status of the asset.
     */
    float oldTime = 0;
    boolean isplaying;
    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        switch (status) {
            case LOADED:
                System.out.println("---118--LOADED ");
                Log.d("SimplePlayer", "Loaded");
                break;
            case DOWNLOADING:
                Log.d("SimplePlayer", "Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");
                System.out.println("---123--Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");;
                break;
            case DOWNLOADED:
                System.out.println("---126--Downloaded to " + asset.getUrl());
                Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
                System.out.println("---130--DOWNLOADCANCELLED");
                Log.d("SimplePlayer", "Download cancelled");
                break;
            case PLAYING:
                hideLoading();
                System.out.println("---135--结束加载的时间 = " + System.currentTimeMillis());
                Log.d("SimplePlayer", "Playing");
//		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                initPlaying();
//				_scrubber.setEnabled(true);
//				_playButton.setText("pause");
                _scrubberMonitorTimer = new Timer();

                final TimerTask task = new TimerTask() {
                    public void run() {
                        if (_updateThumb) {
                            int playbackTime = (int) asset.getPlaybackTime();
                            L.e("playbackTime = " + playbackTime);
                            if (totalDuration <= 0) {
                                mPlayerContralView.setMin(0);
                                totalDuration = (int) asset.getDuration();
                                mPlayerContralView.setMax(totalDuration);
                                mPlayerContralView.setTotalDuration(totalDuration);

                            }
                            mPlayerContralView.setCurTime((int) asset.getPlaybackTime());
                            if (oldTime == asset.getPlaybackTime() && !isPause){
                                showLoading("正在缓冲");
                                System.out.println("----显示loading");
                            } else if (oldTime>asset.getPlaybackTime()){
                                oldTime = asset.getPlaybackTime();
                                hideLoading();
                                System.out.println("----隐藏loading1");

                            }else if (oldTime<asset.getPlaybackTime()){
                                oldTime = asset.getPlaybackTime();
                                hideLoading();
                                System.out.println("----隐藏loading2");
                            }else{
                                hideLoading();
                                System.out.println("----隐藏loading3");
                            }
                            L.e("asset.getPlaybackTime() = "+ asset.getPlaybackTime());
//                            if (asset.getPlaybackTime() == asset.getPlaybackTime() && !isPause){
//                                showLoading("正在缓冲");
//
//                            }else {
//                                hideLoading();
////                                System.out.println("---154--CuurTime = "+asset.getPlaybackTime());
//                            }
//
                        }
                    }
                };

                _scrubberMonitorTimer.schedule(task, 0, 33);
                break;
            case PAUSED:
                Log.d("SimplePlayer", "Paused");
                System.out.println("---162--PAUSED");
//				_playButton.setText("play");
                break;
            case STOPPED:
                Log.d("SimplePlayer", "Stopped");
                System.out.println("---167--STOPPED");
//				_playButton.setText("play");
                _scrubberMonitorTimer.cancel();
                _scrubberMonitorTimer = null;
//				_scrubber.setProgress(0);
//				_scrubber.setEnabled(false);
//		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case COMPLETE:
                Log.d("SimplePlayer", "Complete");
//				_playButton.setText("play");
                if (null != _scrubberMonitorTimer) {
                    _scrubberMonitorTimer.cancel();
                    _scrubberMonitorTimer = null;
                }
                finish();
//		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
            case ERROR:
                Log.d("SimplePlayer", "Error");
                break;
        }
    }

    public void setPlayerContralView(View playerContralView) {
        this.playerContralView = playerContralView;
    }

    private View playerContralView;

    private void initPlaying() {
        System.out.println("---194--initplaying()");
        setPlayerContralView(linCtr);
        mPlayerContralView.setIsPlaying(true);
        ctr_vist = false;
        linCtr.setVisibility(View.GONE);
        mPlayerContralView.setStatusPlay();
        _pfview.setFieldOfView(75);
        mPlayerContralView.setmPlayerContrallerInterface(new PlayerContrallerInterface() {
            @Override
            public void play() {
                if (null != _pfasset) {
                    _pfasset.play();
                    System.out.println("---205--开始播放了");
                    isPause = false;
                }
            }

            @Override
            public void pause() {
                if (null != _pfasset) {
                    _pfasset.pause();
                    isPause = true;
                    System.out.println("---213--暂停播放了");
                }
            }

            @Override
            public void seekTo(int time) {
                if (null != _pfasset) {
//					_pfasset.seekTo(time * 1000);
                    _pfasset.setPLaybackTime(time);
                    System.out.println("---222--seekTo time = " + time);
                }
            }
        });
    }

    /**
     * Click listener for the play/pause button
     */
    private OnClickListener playListener = new OnClickListener() {
        public void onClick(View v) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING) {
                _pfasset.pause();
                System.out.println("---235--暂停播放了");
            } else
                _pfasset.play();
            System.out.println("---238--开始播放了");
        }
    };



    /**
     * Called when pausing the app.
     * This function pauses the playback of the asset when it is playing.
     */
    public void onPause() {
        super.onPause();
        if (_pfasset != null) {
            if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
                _pfasset.pause();
            System.out.println("---253--暂停播放了");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != _pfasset) {
            _pfasset.stop();
            _pfasset.release();
            _pfasset = null;
            System.out.println("---265PlayerVRActivity***onDestroy()");
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_player_360;
    }

    private String title;
    private String titleSplitScreen;

    @Override
    public void init() {
//        if (getIntent().getStringExtra("title").length() > 4){
//            title = getIntent().getStringExtra("title").substring(0,4);
//            System.out.println("---length = " + title.length());
//        }else {
            title =  getIntent().getStringExtra("title");
//        }
        mPlayerContralView1 = new PlayerContralView(this, PlayerContralView.TYPE_360);
        mPlayerContralView2 = new PlayerContralView(this, PlayerContralView.TYPE_360);
        mPlayerContralView = new PlayerCtrMnger(mPlayerContralView1, mPlayerContralView2);
        setPlayerContralView(linCtr);
        System.out.println("***PlaylerVRActivity***setPlayerContralView()");
        controller1.addView(mPlayerContralView1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        controller2.addView(mPlayerContralView2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//            loadVideo(getIntent().getData().getPath());
//            System.out.println("***PlayerVRActivity***filename " + getIntent().getData().getPath());
//        mPlayerContralView1.setTitle(title);
//        mPlayerContralView2.setTitle(title);
        if (curMode == MODE_NORMAL) {
            mPlayerContralView1.setTitle(title);
            mPlayerContralView2.setTitle(title);
            mPlayerContralView1.setSplitScreen(true);
            mPlayerContralView2.setSplitScreen(true);
            controller2.setVisibility(View.GONE);
        } else {
            if (title.length() > 4){
                titleSplitScreen = title.substring(0, 4);
                System.out.println("---length = " + titleSplitScreen.length());
            }else {
                titleSplitScreen =  title;
            }
            mPlayerContralView1.setSplitScreen(false);
            mPlayerContralView2.setSplitScreen(false);
            controller2.setVisibility(View.VISIBLE);
            mPlayerContralView1.setTitle(titleSplitScreen);
            mPlayerContralView2.setTitle(titleSplitScreen);
        }
        mPlayerContralView.setChangeMode(new ChangeModeListener() {
            @Override
            public void clickTouchMode() {
                System.out.println("***PlayerVRActivity***clickTouchMode()");
                if (_currentNavigationMode == PFNavigationMode.TOUCH) {
                    mPlayerContralView1.setTouchMode(true);
                    mPlayerContralView2.setTouchMode(true);
                    _currentNavigationMode = PFNavigationMode.MOTION;
                } else {
                    mPlayerContralView1.setTouchMode(false);
                    mPlayerContralView2.setTouchMode(false);
                    _currentNavigationMode = PFNavigationMode.TOUCH;
                }
                _pfview.setNavigationMode(_currentNavigationMode);
            }

            @Override
            public void clickSplitScreen() {
                System.out.println("***PlayerVRActivity***clickSplitScreen()");
                if (curMode == MODE_NORMAL) {
                    if (title.length() > 4){
                        titleSplitScreen = title.substring(0, 4);
                        System.out.println("---length = " + titleSplitScreen.length());
                    }else {
                        titleSplitScreen =  title;
                    }
                    mPlayerContralView1.setTitle(titleSplitScreen);
                    mPlayerContralView2.setTitle(titleSplitScreen);
                    curMode = MODE_SPLIT_SCREEN;
                    mPlayerContralView1.setSplitScreen(false);
                    mPlayerContralView2.setSplitScreen(false);
                    controller2.setVisibility(View.VISIBLE);
                } else {
                    mPlayerContralView1.setSplitScreen(true);
                    controller2.setVisibility(View.GONE);
                    curMode = MODE_NORMAL;
                    mPlayerContralView1.setTitle(title);
                    mPlayerContralView2.setTitle(title);

                }

                _pfview.setMode(curMode, 0);

            }
        });
        if (getIntent().getData() == null) {
            loadVideo(getIntent().getStringExtra("play_url"));
            System.out.println("---337--PlayerVRActivity***filename " + getIntent().getStringExtra("play_url"));
        } else {
            loadVideo(getIntent().getData().getPath());
            System.out.println("---340--PlayerVRActivity***filename " + getIntent().getData().getPath());
        }
        mPlayerContralView1.setTitle(title);
        mPlayerContralView2.setTitle(title);
//        System.out.println("--titleLength = " + title.length());

    }

    @Override
    public void getIntentData(Intent intent) {
        boolean b = intent.getBooleanExtra("splite_screen", false);
        if (b) {
            curMode = MODE_SPLIT_SCREEN;
        } else {
            curMode = MODE_NORMAL;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        _pfview.release();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param seekbar  The SeekBar whose progress has changed
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
    }

    /**
     * Notification that the user has started a touch gesture.
     * In this function we signal the timer not to update the playback thumb while we are adjusting it.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStartTrackingTouch(SeekBar seekbar) {
        System.out.println("***PlayerVRActivity*** onStartTrackingTouch()");
        _updateThumb = false;
    }

    /**
     * Notification that the user has finished a touch gesture.
     * In this function we request the asset to seek until a specific time and signal the timer to resume the update of the playback thumb based on playback.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStopTrackingTouch(SeekBar seekbar) {
        System.out.println("***PlayerVRActivity*** onStopTrackingTouch()");
        _updateThumb = true;
    }

    double nLenStart = 0;
    //    当前视距值
    float fov = 75;
    //    视距缩放值
    float bfov = 0;
    double lastTime = 0;


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            System.out.println("--双手指不拦截");
            int nCnt = event.getPointerCount();
            System.out.println("----事件响应");

            int n = event.getAction();
            if (n == MotionEvent.ACTION_MOVE) {
                System.out.println("----移动");
            }
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt) {
                System.out.println("----双手指Down");

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                nLenStart = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                lastTime = 0;

            } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && 2 == nCnt) {
                System.out.println("----双手指移动");

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                bfov = (float) (nLenEnd - nLenStart);
                if (lastTime == 0 || Math.abs((int) nLenEnd - lastTime) > 2) {
                    lastTime = nLenEnd;
                    if (nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
                    {
                        float in = fov - bfov / 180;
                        if (in > 30) {
                            _pfview.setFieldOfView(in);
                            fov = in;
                        } else {
                            _pfview.setFieldOfView(30);
                            fov = 30;
                        }
                    } else {
                        float in = fov - bfov / 120;
                        if (in < 150) {
                            _pfview.setFieldOfView(in);
                            fov = in;
                        } else {
                            _pfview.setFieldOfView(150);
                            fov = 150;
                        }
                    }
                }

            } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && 2 == nCnt) {

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                System.out.println("----差值---" + (nLenEnd - nLenStart));

            }
            return true;
        } else {
            System.out.println("单手指触碰");
            if(!ctr_vist && linCtr != null){
//            playerContralView.show();
                ctr_vist = true;
                linCtr.setVisibility(View.VISIBLE);
                System.out.println("显示进度条");
            }else {
                ctr_vist = false;
                linCtr.setVisibility(View.GONE);
                System.out.println("隐藏进度条");
            }
            delayedHide(4000);
            ctr_vist = false;
            return super.dispatchTouchEvent(event);
        }
    }
    private void delayedHide(int i) {
        mHideSystemUIHandler.removeMessages(0);
        mHideSystemUIHandler.sendEmptyMessageDelayed(0, i);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.e("---keycode = "+keyCode);
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
                finish();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                if (_pfasset != null) {
                    if (_pfasset.getStatus() == PFAssetStatus.PLAYING) {
                        _pfasset.pause();
                        this.showToast("暂停");
                        L.e("---你点击了暂停");
                    } else {
                        _pfasset.play();
                        this.showToast("播放");
                        L.e("---你点击了播放");
                    }
                }

                break;
        }
        return true;
    }

}
