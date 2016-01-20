package com.hotcast.vr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lostnote on 15/11/23.
 */
public class MyViewPager extends LazyViewPager {
    private boolean isCanScroll = true;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyViewPager(Context context){
        super(context);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
