package com.hotcast.vr.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by liurongzhi on 2016/2/19.
 */
public class GalleyAdapter extends BaseAdapter {
    private List<ImageView> imgs;
    public GalleyAdapter(List<ImageView> imgs) {
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return imgs.get(position);
    }
}
