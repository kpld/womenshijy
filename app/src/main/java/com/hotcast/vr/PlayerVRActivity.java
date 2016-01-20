package com.hotcast.vr;

import java.util.*;

import com.hotcast.vr.pageview.ChangeModeListener;
import com.hotcast.vr.pageview.PlayerContralView;
import com.hotcast.vr.tools.L;
import com.panframe.android.lib.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.SeekBar.*;

import butterknife.InjectView;

public class PlayerVRActivity extends BaseLanActivity implements PFAssetObserver, OnSeekBarChangeListener {

    PFView _pfview;
    PFAsset _pfasset;
    PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;

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
    private boolean ctr_vist = true;//控制条为显示状态
    public static final int MODE_SPLIT_SCREEN = 2;
    public static final int MODE_NORMAL = 0;
    private int curMode = MODE_SPLIT_SCREEN;

    private String title;

    /**
     * Start the video with a local file path
     *
     * @param filename The file path on device storage
     */
    public void loadVideo(String filename) {

        L.e("filename=" + filename);
        _pfview = PFObjectFactory.view(this);
        _pfview.setMode(curMode, 0);
        _pfview.setFieldOfView(150);
        _pfasset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);

        _pfview.displayAsset(_pfasset);
        _pfview.setNavigationMode(_currentNavigationMode);
//        _pfview.setFieldOfView(100);

        _frameContainer.addView(_pfview.getView(), 0);

        _pfasset.play();

        System.out.println("---73--开始加载的时间 = " + System.currentTimeMillis());

        showLoading("正在加载...");
    }

    /**
     * Status callback from the PFAsset instance.
     * Based on the status this function selects the appropriate action.
     *
     * @param asset  The asset who is calling the function
     * @param status The current status of the asset.
     */
    public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
        switch (status) {
            case LOADED:
//                showLoading("正在努力加载中");
                System.out.println("---89--LOADED 正在加载");
                Log.d("SimplePlayer", "Loaded");
                break;
            case DOWNLOADING:
                Log.d("SimplePlayer", "Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");
                System.out.println("---94--Downloading 360� movie: " + _pfasset.getDownloadProgress() + " percent complete");
                break;
            case DOWNLOADED:
                Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
                System.out.println("---98--Downloaded to " + asset.getUrl());
                break;
            case DOWNLOADCANCELLED:
                Log.d("SimplePlayer", "Download cancelled");
                System.out.println("---102--DOWNLOADCANCELLED");
                break;
            case PLAYING:
                hideLoading();
                System.out.println("---106--结束加载的时间 = " + System.currentTimeMillis());
                Log.d("SimplePlayer", "Playing");
                initPlaying();
                _scrubberMonitorTimer = new Timer();
                final TimerTask task = new TimerTask() {
                    public void run() {
                        if (_updateThumb) {
                            if (totalDuration <= 0) {
                                mPlayerContralView.setMin(0);
                                totalDuration = (int) asset.getDuration();
                                mPlayerContralView.setMax(totalDuration);
                                mPlayerContralView.setTotalDuration(totalDuration);
                            }
                            mPlayerContralView.setCurTime((int) asset.getPlaybackTime());

//                            System.out.println("---120--CuurTime = "+asset.getPlaybackTime());
                        }
                    }
                };
                _scrubberMonitorTimer.schedule(task, 0, 33);
                break;
            case PAUSED:
                showLoading("正在努力加载中");
                System.out.println("---127--PAUSED");
                Log.d("SimplePlayer", "Paused");
//				_playButton.setText("play");
                break;
            case STOPPED:
                Log.d("SimplePlayer", "Stopped");
                System.out.println("---133--STOPPED");
                _scrubberMonitorTimer.cancel();
                _scrubberMonitorTimer = null;
                break;
            case COMPLETE:
                Log.d("SimplePlayer", "Complete");
                if (null != _scrubberMonitorTimer) {
                    _scrubberMonitorTimer.cancel();
                    _scrubberMonitorTimer = null;
                }
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
        System.out.println("---156--initplaying()");
        setPlayerContralView(linCtr);
        mPlayerContralView.setIsPlaying(true);
        linCtr.setVisibility(View.GONE);
        ctr_vist = false;
        mPlayerContralView.setStatusPlay();
        mPlayerContralView.setmPlayerContrallerInterface(new PlayerContrallerInterface() {
            @Override
            public void play() {
                if (null != _pfasset) {
//                    hideLoading();
                    _pfasset.play();
                    System.out.println("---167--开始播放了");
                }
            }

            @Override
            public void pause() {
                if (null != _pfasset) {
                    _pfasset.pause();
                    System.out.println("---175--暂停播放了");
//                    showLoading("正在努力加载中");
                }
            }

            @Override
            public void seekTo(int time) {
                if (null != _pfasset) {
                    _pfasset.setPLaybackTime(time);
                    System.out.println("---184--播放到time = "+time);
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
                System.out.println("---197--暂停播放了");
            } else
                _pfasset.play();
            System.out.println("---200--开始播放了");
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
            System.out.println("---215--暂停播放了");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != _pfasset) {
            _pfasset.stop();
            _pfasset.release();
            _pfasset = null;
            System.out.println("---226--PlayerVRActivity***onDestroy()");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_player_360;
    }

    @Override
    public void init() {
//        if (!TextUtils.isEmpty(getIntent().getStringExtra("play_url") )){
//            loadVideo(getIntent().getStringExtra("play_url"));
//        }{
//            finish();
//        }
        loadVideo(getIntent().getStringExtra("play_url"));
        title = getIntent().getStringExtra("title");
        mPlayerContralView1 = new PlayerContralView(this, PlayerContralView.TYPE_360);
        mPlayerContralView2 = new PlayerContralView(this, PlayerContralView.TYPE_360);
//        System.out.println("***playVRActivity mPlayerContralView1 = "+ mPlayerContralView1);
//        System.out.println("***playVRActivity mPlayerContralView2 = "+ mPlayerContralView2);

        mPlayerContralView = new PlayerCtrMnger(mPlayerContralView1, mPlayerContralView2);

//        System.out.println("***playVRActivity mPlayerContralView1 = "+ mPlayerContralView);

        setPlayerContralView(linCtr);
        controller1.addView(mPlayerContralView1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        controller2.addView(mPlayerContralView2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mPlayerContralView1.setTitle(title);
        mPlayerContralView2.setTitle(title);

        if (curMode == MODE_NORMAL) {
            mPlayerContralView1.setSplitScreen(true);
            mPlayerContralView2.setSplitScreen(true);
            controller2.setVisibility(View.GONE);
        } else {
            mPlayerContralView1.setSplitScreen(false);
            mPlayerContralView2.setSplitScreen(false);
            controller2.setVisibility(View.VISIBLE);
        }
        mPlayerContralView.setChangeMode(new ChangeModeListener() {
            @Override
            public void clickTouchMode() {
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
                if (curMode == MODE_NORMAL) {
                    curMode = MODE_SPLIT_SCREEN;
                    mPlayerContralView1.setSplitScreen(false);
                    mPlayerContralView2.setSplitScreen(false);
                    controller2.setVisibility(View.VISIBLE);
                } else {
                    mPlayerContralView1.setSplitScreen(true);
                    controller2.setVisibility(View.GONE);
                    curMode = MODE_NORMAL;
                }

                _pfview.setMode(curMode, 0);

            }
        });
        if (getIntent().getData() == null) {
            loadVideo(getIntent().getStringExtra("play_url"));
            System.out.println("---303--PlayerVRActivity***filename " + getIntent().getStringExtra("play_url"));
        } else {
            loadVideo(getIntent().getData().getPath());
            System.out.println("---306--PlayerVRActivity***filename " + getIntent().getData().getPath());
        }
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
        _updateThumb = false;
    }

    /**
     * Notification that the user has finished a touch gesture.
     * In this function we request the asset to seek until a specific time and signal the timer to resume the update of the playback thumb based on playback.
     *
     * @param seekbar The SeekBar in which the touch gesture began
     */
    public void onStopTrackingTouch(SeekBar seekbar) {
        _updateThumb = true;
    }

    double nLenStart = 0;
//    当前视距值
    float fov = 75;
//    视距缩放值
    float bfov = 0;
    double lastTime =0;
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
               lastTime =0;

            } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE && 2 == nCnt) {
                System.out.println("----双手指移动");

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                bfov = (float) (nLenEnd - nLenStart);
                if (lastTime == 0 || Math.abs((int) nLenEnd - lastTime)>2){
                    lastTime=nLenEnd;
                    if (nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
                    {
                        float in = fov - bfov/180;
                        if (in >30){
                            _pfview.setFieldOfView(in);
                            fov=in;
                        }else {
                            _pfview.setFieldOfView(30);
                            fov=30;
                        }
                    } else {
                        float in = fov - bfov/120 ;
                        if (in < 150){
                            _pfview.setFieldOfView(in);
                            fov=in;
                        }else {
                            _pfview.setFieldOfView(150);
                            fov=150;
                        }
                    }
                }

            }else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP && 2 == nCnt) {

                int xlen = Math.abs((int) event.getX(0) - (int) event.getX(1));
                int ylen = Math.abs((int) event.getY(0) - (int) event.getY(1));

                double nLenEnd = Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                System.out.println("----差值---"+(nLenEnd-nLenStart));

            }
            return true;
        } else {
            System.out.println("单手指触碰");
            return super.dispatchTouchEvent(event);
        }
    }
}
