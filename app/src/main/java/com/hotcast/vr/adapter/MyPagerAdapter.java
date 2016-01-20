package com.hotcast.vr.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by joey on 14/11/18.
 */
public class MyPagerAdapter extends BasePagerAdapter {
    private List<View> views;
    private List<String> titles;

    public MyPagerAdapter(List<View> views,List<String> titles) {
        this.views = views;
        this.titles = titles;
    }
    public MyPagerAdapter(List<View> views) {
        this.views = views;
    }

    public void setData(List<View> views) {
        this.views = views;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getRealCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}