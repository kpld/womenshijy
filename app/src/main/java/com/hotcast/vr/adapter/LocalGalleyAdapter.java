package com.hotcast.vr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.hotcast.vr.R;
import com.hotcast.vr.tools.DensityUtils;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by liurongzhi on 2016/2/19.
 */
public class LocalGalleyAdapter extends BaseAdapter {
    private List<String> imgs;
    BitmapUtils bitmapUtils;
    Context context;

    public LocalGalleyAdapter(List<String> imgs, Context context) {
        this.imgs = imgs;
        this.context = context;
        bitmapUtils = new BitmapUtils(context);
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
        if (convertView == null){
            convertView = new ImageView(context);
            ((ImageView)(convertView)).setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(context, 118), DensityUtils.dp2px(context, 80)));
            ((ImageView)(convertView)).setPadding(DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8));
            ((ImageView)(convertView)).setScaleType(ImageView.ScaleType.FIT_XY);
            ((ImageView)(convertView)).setBackgroundResource(R.drawable.buttom_selector_second);
        }
        bitmapUtils.display(((ImageView)convertView),imgs.get(position));

        return convertView;
    }

}
