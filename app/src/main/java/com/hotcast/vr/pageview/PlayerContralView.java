package com.hotcast.vr.pageview;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.PlayerContrallerInterface;
import com.hotcast.vr.R;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/12/15.
 */
public class PlayerContralView extends BaseView {
    @InjectView(R.id.btnBack)
    ImageView btnBack;
    @InjectView(R.id.tv_name)
    TextView tv_name;
    @InjectView(R.id.btnPlay)
    ImageView btnPlay;
    @InjectView(R.id.seekbar)
    DiscreteSeekBar seekbar;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.view)
    View view;
    @InjectView(R.id.ivTouchMode)
    ImageView touchMode;
    @InjectView(R.id.ivSplitScreen)
    ImageView splitScreen;
    @OnClick(R.id.btnBack)
    void clickBack() {
        activity.finish();
    }

    @OnClick(R.id.ivSplitScreen)
    void clickSplite(){
        if(null != changeMode){
            changeMode.clickSplitScreen();
        }

    }
    public void setSplitScreen(boolean isSplitScreen){
        if (isSplitScreen){
            splitScreen.setImageResource(R.mipmap.screen);
        }else {
            splitScreen.setImageResource(R.mipmap.split);
        }
    }
    @OnClick(R.id.ivTouchMode)
    void clickTouchMode(){
        if(null != changeMode){
            changeMode.clickTouchMode();
        }
    }
    public void setTouchMode(boolean isTouch){
        if ( isTouch){
            touchMode.setImageResource(R.mipmap.rotate);
        }else {
            touchMode.setImageResource(R.mipmap.touch_mode);
        }
    }

    @OnClick(R.id.btnPlay)
    void clickPlay(){
        if(isPlaying()){
            if(curStatus == STATUS_PLAYING){
                setStatusStop();
                if(mPlayerContrallerInterface != null){
                    mPlayerContrallerInterface.pause();
                }
            }else{
                setStatusPlay();
                if(mPlayerContrallerInterface != null){
                    mPlayerContrallerInterface.play();
                }
            }
        }
    }

    private boolean isPlaying;
    private int totalDuration;
    private int curTime;
    private int min = 0, max = 0;
    private String totalTimeStr;
    private boolean bTouched;
//    private boolean isShowing;
    private int curStatus = STATUS_IDEL;

    private int seekPos;
    public static final int STATUS_IDEL = -1;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_STOP = 0;
    public static final int TYPE_360 = 111, TYPE_3D = 222, TYPE_IMAX = 333;
    private int type;

    private ChangeModeListener changeMode;


    public void setChangeMode(ChangeModeListener changeMode) {
        this.changeMode = changeMode;
    }


    public PlayerContralView(BaseActivity activity, int type) {
        super(activity, R.layout.player_contral);
        this.type = type;
        switch (type){
            case TYPE_360:
                splitScreen.setVisibility(View.VISIBLE);
                touchMode.setVisibility(View.VISIBLE);
                break;
            case TYPE_3D:
                splitScreen.setVisibility(View.GONE);
                touchMode.setVisibility(View.GONE);
                break;
            case TYPE_IMAX:
                splitScreen.setVisibility(View.VISIBLE);
                touchMode.setVisibility(View.GONE);
                break;
        }
        seekbar.setIndicatorPopupEnabled(false);
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                L.e("seek pos=" + i);
                seekPos = i;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                setbTouched(true);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                setbTouched(false);
                if (null != mPlayerContrallerInterface) {
                    mPlayerContrallerInterface.seekTo(seekPos);
                }
            }
        });
    }


    private boolean isbTouched() {
        return bTouched;
    }

    private void setbTouched(boolean bTouched) {
        this.bTouched = bTouched;
    }

    public void setStatusPlay() {
        btnPlay.setImageResource(R.mipmap.stop);
        curStatus = STATUS_PLAYING;
    }

    public void setStatusStop() {
        btnPlay.setImageResource(R.mipmap.play);
        curStatus = STATUS_STOP;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String cur = getTimeString(curTime);
            if (Utils.textIsNull(totalTimeStr)) {
                totalTimeStr = getTimeString(totalDuration);
            }
            tvTime.setText(cur + "/" + totalTimeStr);
            if(!isbTouched()){
                seekbar.setProgress(curTime);
            }
        }
    };
    public void setCurTime(int curTime) {
        this.curTime = curTime;
        handler.sendEmptyMessage(0);
    }


    public void setmPlayerContrallerInterface(PlayerContrallerInterface mPlayerContrallerInterface) {
        this.mPlayerContrallerInterface = mPlayerContrallerInterface;
    }

    private PlayerContrallerInterface mPlayerContrallerInterface;

    private boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    public void setTitle(String title){
        tv_name.setText(title);
    }

    private String getTimeString(int time) {
        return getHstr(time) + ":" + getMstr(time) + ":" + getSstr(time);
    }

    private String getHstr(int time) {
        int h = time / 60 / 60;
        return String.format("%02d", h);
    }

    private String getMstr(int time) {
        int m = time / 60 % 60;
        return String.format("%02d", m);
    }

    private String getSstr(int time) {
        int s = time % 60;
        return String.format("%02d", s);
    }

    public void setMin(int min) {
        this.min = min;
        seekbar.setMin(min);
    }

    public void setMax(int max) {
        this.max = max;
        seekbar.setMax(max);
    }

    public void show() {
        view.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view.setVisibility(View.GONE);
    }
}
