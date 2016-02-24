package com.hotcast.vr.pageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by liurongzhi on 2016/2/19.
 */
public class NoTouchView extends RelativeLayout {

    public NoTouchView(Context context) {
        super(context);
    }

    public NoTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoTouchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        System.out.println("---根view不分发事件");
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        System.out.println("---根view不处理事件");
        return super.onTouchEvent(event);
    }
}
