package com.hotcast.vr.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by joey on 14/10/27.
 */
public abstract class BasePagerAdapter extends PagerAdapter {


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }

    public abstract int getRealCount();
}
