package com.hotcast.vr.pageview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.PlayerVRActivityNew;
import com.hotcast.vr.R;
import com.hotcast.vr.VerticalGallery.ImageAdapter;
import com.hotcast.vr.adapter.GalleyAdapter;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.L;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/2/20.
 */
public class GalleryItemView extends RelativeLayout {
    Handler mhandler;
    private List<ImageView> mImages;
    List<String> titles = new ArrayList<>();
    List<String> descs = new ArrayList<>();
    List<ChannelList> tmpList;
    Context context;
    BitmapUtils bitmapUtils;
    LandGalleyView gallery1;
    View loadingBar1;
    View nointernet1;
    boolean isloading = false;
    TextView tv_title1;
    TextView tv_desc1;
    TextView bt_ceach1;
    TextView tv_notice_content;
    //    TextView tv_page1;
    GalleyAdapter adapter1;
    List<String> localUrlList;

    public List<String> getLocalUrlList() {
        return localUrlList;
    }

    public void setLocalUrlList(List<String> localUrlList) {
        this.localUrlList = localUrlList;
    }
    public GalleryItemView(Context context, List<ChannelList> tmpList, List<String> titles, List<String> descs) {
        super(context);
        bitmapUtils = new BitmapUtils(context);
        this.context = context;
        this.tmpList = tmpList;
        this.titles = titles;
        this.descs = descs;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.verticalgalleryitem, this);
        initData();
        initView();
    }

    //设置文字布局
    public void setText(int p) {
        tv_title1.setText(titles.get(p));
        tv_desc1.setText(titles.get(p));
        if (localUrlList.contains(tmpList.get(p).getVideos().get(0).getVid())){
            bt_ceach1.setText("已下载");
            bt_ceach1.setBackgroundResource(R.drawable.download_button_bluel);
        }else{
            bt_ceach1.setText("未下载");
            bt_ceach1.setBackgroundResource(R.drawable.download_button_nomal);
        }
    }

    /**
     * 初始化数据
     */
    public void initData() {
        mImages = new ArrayList<>();
        for (int i = 0; i < tmpList.size(); i++) {
            ImageView iv2 = new ImageView(context);
            bitmapUtils.display(iv2, tmpList.get(i).getImage().get(0));
            iv2.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(context, 118), DensityUtils.dp2px(context, 80)));
            iv2.setPadding(DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8), DensityUtils.dp2px(context, 8));
            iv2.setScaleType(ImageView.ScaleType.FIT_XY);
            iv2.setBackgroundResource(R.drawable.buttom_selector_second);
            mImages.add(iv2);
        }
    }

    public void initView() {
        gallery1 = (LandGalleyView) findViewById(R.id.gallery1);
        loadingBar1 = findViewById(R.id.loadingbar1);
        nointernet1 = findViewById(R.id.nointernet1);
        tv_title1 = (TextView) findViewById(R.id.tv_title1);
        tv_desc1 = (TextView) findViewById(R.id.tv_desc1);
        bt_ceach1 = (TextView) findViewById(R.id.bt_ceach1);
//        tv_page1 = (TextView) findViewById(R.id.tv_page1);
        tv_notice_content = (TextView) findViewById(R.id.tv_notice_content);
        adapter1 = new GalleyAdapter(mImages);
        gallery1.setAdapter(adapter1);
        if (mImages.size() > 2) {
            gallery1.setSelection(1);
        }
        showOrHideLoadingBar(false);
        showNoInternetDialog(false);
        gallery1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setText(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showOrHideLoadingBar(boolean flag) {
        if (flag) {
            isloading = flag;
            loadingBar1.setVisibility(View.VISIBLE);
        } else {
            isloading = flag;
            loadingBar1.setVisibility(View.GONE);
        }
    }

    public void showNoInternetDialog(boolean flag) {
        tv_notice_content.setText("网络连接异常");
        if (flag) {
            nointernet1.setVisibility(View.VISIBLE);
            mhandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            nointernet1.setVisibility(View.GONE);
        }
    }

    public void showNoDataDialog(String text) {
        tv_notice_content.setText(text);
        nointernet1.setVisibility(View.VISIBLE);
        mhandler.sendEmptyMessageDelayed(0, 2000);
    }

    public void setHandler(Handler handler) {
        this.mhandler = handler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gallery1.onTouchEvent(event);
    }

    public int getSelectedItemPosition() {
        return gallery1.getSelectedItemPosition();
    }

    public void clickVideo() {
        String vid = tmpList.get(getSelectedItemPosition()).getVideos().get(0).getVid();
        Intent intent = new Intent(context, PlayerVRActivityNew.class);
        intent.putExtra("vid", vid);
        intent.putExtra("splite_screen", true);
        context.startActivity(intent);
    }

    public String downVideoData() {
        return tmpList.get(getSelectedItemPosition()).getVideos().get(0).getVid();

    }

    public String getImgurl() {
        return tmpList.get(getSelectedItemPosition()).getImage().get(0);
    }
    public String getNowId() {
        return tmpList.get(getSelectedItemPosition()).getVideos().get(0).getVid();
    }
}
