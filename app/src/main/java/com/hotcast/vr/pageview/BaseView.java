package com.hotcast.vr.pageview;

import android.view.LayoutInflater;
import android.view.View;

import com.hotcast.vr.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by joey on 14/11/18.
 */
public class BaseView {
    private final String PAGE_NAME = getClass().getName();

    protected BaseActivity activity;
    protected View rootView;
    protected boolean bFirstInit = true, bDataProcessed, bProcessing;

    protected boolean checkRequest() {
        return !bDataProcessed && !bProcessing;
    }



    public View getTabView() {
        return tabView;
    }

    public void setTabView(View tabView) {
        this.tabView = tabView;
    }

    private View tabView;

    public BaseView(BaseActivity activity, int id) {
        this.activity = activity;

        rootView = LayoutInflater.from(activity).inflate(id, null, false);
        ButterKnife.inject(this, rootView);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    private int tag;


    public View getRootView() {
        return rootView;
    }


    public BaseActivity getActivity() {
        return activity;
    }

    public void init()   {
        bFirstInit = false;
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void visGone(View vis, View gone) {
        if (null != vis) {
            vis.setVisibility(View.VISIBLE);
        }

        if (null != gone) {
            gone.setVisibility(View.GONE);
        }
    }
}
