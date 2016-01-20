package com.hotcast.vr;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hotcast.vr.tools.L;

/**
 * Created by joey on 8/10/15.
 */
public abstract class BaseLanActivity extends BaseActivity implements View.OnSystemUiVisibilityChangeListener{

    private View playerContralView;

    public void setPlayerContralView(View playerContralView) {
        this.playerContralView = playerContralView;
    }

    private Handler mHideSystemUIHandler = new Handler() {

        public void handleMessage(Message message) {
            getWindow().getDecorView().setSystemUiVisibility(1542);
            if(null != playerContralView){
//                playerContralView.hide();
                playerContralView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(128);
        getWindow().getDecorView().setSystemUiVisibility(1542);
    }

    public void onSystemUiVisibilityChange(int i) {
        L.e("Test", (new StringBuilder("onVisChg")).append(i).toString());
        if (i == 0){
            if(null != playerContralView){
//                playerContralView.show();
                playerContralView.setVisibility(View.VISIBLE);
            }
//            delayedHide(4000);
            onTouchEvent(null);
        }
    }

//    protected abstract void onScreenTouched();
    public boolean onTouchEvent(MotionEvent motionevent) {
//        if(null != motionevent && motionevent.getAction() == MotionEvent.ACTION_DOWN){
//            onScreenTouched();
//        }
        if(null != playerContralView){
//            playerContralView.show();
            playerContralView.setVisibility(View.VISIBLE);
            System.out.println("显示控制条");
        }
        delayedHide(4000);
        System.out.println("隐藏控制条");
        return true;
    }

    private void delayedHide(int i) {
        mHideSystemUIHandler.removeMessages(0);
        mHideSystemUIHandler.sendEmptyMessageDelayed(0, i);
    }
}
