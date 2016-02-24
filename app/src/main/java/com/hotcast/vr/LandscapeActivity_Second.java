package com.hotcast.vr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.VerticalGallery.ImageAdapter;
import com.hotcast.vr.VerticalGallery.VerticalGallery;
import com.hotcast.vr.VerticalGallery.VerticalGalleryAdapterView;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.pageview.GalleryItemView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.List;

public class LandscapeActivity_Second extends BaseActivity {
    List<ChannelList> tmpList;
    String channel_id;//当前频道的ID
    boolean isloading;
    boolean nodata = true;//是否有数据
    private List<String> titles = new ArrayList<>();
    private List<String> descs = new ArrayList<>();
    private VerticalGallery vg1;
    private VerticalGallery vg2;
    private int nowPosition;
    private int nowPage = 0;
    //    private GalleryItemView itemView1;
//    private GalleryItemView itemView2;
    //    private GalleryItemView upitemView1;
    private GalleryItemView downitemView1;
    //    private GalleryItemView upitemView2;
    private GalleryItemView downitemView2;
    ImageAdapter adapter1;
    ImageAdapter adapter2;
    List<GalleryItemView> views1;
    List<GalleryItemView> views2;
    View nointernet1;
    View nointernet2;
    TextView tv_page1;
    TextView tv_page2;
    DbUtils db;
    List<String> localUrlList;
    List<LocalBean> dbList;

    @Override

    public int getLayoutId() {
        return R.layout.activity_landscape_activity__second;
    }

    @Override
    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        views1 = new ArrayList<>();
        views2 = new ArrayList<>();
        db = DbUtils.create(this);
        localUrlList = new ArrayList<>();
        try {
            dbList = db.findAll(LocalBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        } else {
            for (LocalBean localBean : dbList) {
                System.out.println("---localBean_url:" + localBean.getUrl());
                localUrlList.add(localBean.getId());
            }
        }
        initView();
    }

    public void initView() {
        vg1 = (VerticalGallery) findViewById(R.id.vg1);
        vg2 = (VerticalGallery) findViewById(R.id.vg2);
        int size = 0;
        while (size < tmpList.size()) {
            if (size + 8 > tmpList.size()) {
                size = tmpList.size();
            } else {
                size = size + 8;
            }
            int start = 0;
            if (size - 8 < 0) {
                start = 0;
            } else {
                start = size - 8;
            }
            GalleryItemView itemView1 = new GalleryItemView(this, tmpList.subList(start, size), titles.subList(start, size), descs.subList(start, size));
            GalleryItemView itemView2 = new GalleryItemView(this, tmpList.subList(start, size), titles.subList(start, size), descs.subList(start, size));
            itemView1.setHandler(mhandler);
            itemView1.setLocalUrlList(localUrlList);
            itemView2.setHandler(mhandler);
            itemView2.setLocalUrlList(localUrlList);
            views1.add(itemView1);
            views2.add(itemView2);
        }
//        views2.add(downitemView2);
        adapter1 = new ImageAdapter(this, views1);
        adapter2 = new ImageAdapter(this, views2);
        vg1.setAdapter(adapter1);
        vg2.setAdapter(adapter2);
        vg1.setOnScrollStopListener(new VerticalGallery.OnScrollStopListener() {
            @Override
            public void onScrollStop(int p) {
                setText(nowPage);
            }
        });
        vg2.setOnScrollStopListener(new VerticalGallery.OnScrollStopListener() {
            @Override
            public void onScrollStop(int p) {

            }
        });

        tv_page1 = (TextView) findViewById(R.id.tv_page1);
        tv_page2 = (TextView) findViewById(R.id.tv_page2);

        nointernet1 = findViewById(R.id.nointernet1);
        nointernet2 = findViewById(R.id.nointernet2);
        vg1.setOnItemSelectedListener(new VerticalGalleryAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(VerticalGalleryAdapterView<?> parent, View view, int position, long id) {
                nowPage = position;
            }

            @Override
            public void onNothingSelected(VerticalGalleryAdapterView<?> parent) {

            }
        });
    }

    //设置文字布局
    public void setText(int nowPage) {
        tv_page1.setText("第" + (nowPage + 1) + "页");
        tv_page2.setText("第" + (nowPage + 1) + "页");
    }

    /**
     * 改变影片的信息及状态
     */
    public void changeVideoInfo() {

    }

    @Override
    public void getIntentData(Intent intent) {
        tmpList = (List<ChannelList>) getIntent().getSerializableExtra("tmpList");
        System.out.println("---数据的尺寸：" + tmpList.size());
        channel_id = getIntent().getStringExtra("channel_id");
        if (tmpList.size() == 0) {
            nodata = false;
        } else {
            for (int i = 0; i < tmpList.size(); i++) {
                ChannelList vrPlay = tmpList.get(i);
                titles.add(vrPlay.getTitle());
                descs.add(vrPlay.getDesc());
            }
        }
    }

    int downX;
    int moveX;
    int upX;
    int downY;
    int moveY;
    int upY;

    boolean nOrp = false;//false 表示next

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                upY = (int) event.getY();
                if (downY < upY) {
                    nOrp = true;
                } else {
                    nOrp = false;
                }
                break;
        }
        int xlen = Math.abs(downX - upX);
        int ylen = Math.abs(downY - upY);
        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
        int lengthY = Math.abs(moveY - downY);
        int lengthX = Math.abs(moveX - downX);
//        System.out.println("---length:" + length);
        if (length < 20 && !isloading) {
            //执行点击事件
            clickItem();
            System.out.println("---点击事件");
            return true;
        }
        if (lengthX > lengthY) {
            views1.get(nowPage).onTouchEvent(event);
            views2.get(nowPage).onTouchEvent(event);
        } else if (lengthX < lengthY && lengthY > 100) {
            vg1.onTouchEvent(event);
            vg2.onTouchEvent(event);
        }
        return true;
    }

    public void clickItem() {
        showOrHideLoadingBar(true);
        String url = views1.get(nowPage).getImgurl();
        getplayUrl(views1.get(nowPage).downVideoData(), url);

    }

    public void showOrHideLoadingBar(boolean flag) {
        views1.get(nowPage).showOrHideLoadingBar(flag);
        views1.get(nowPage).showOrHideLoadingBar(flag);
    }

    public void showNoInternetDialog(boolean flag) {
        views1.get(nowPage).showNoInternetDialog(flag);
        views2.get(nowPage).showNoInternetDialog(flag);
    }

    public void showNoDataDialog(String text) {
        views1.get(nowPage).showNoDataDialog(text);
        views2.get(nowPage).showNoDataDialog(text);
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showNoInternetDialog(false);
                    break;
                case 1:
                    break;
            }
        }
    };

    public void getNetData(final String channel_id, int page, boolean nOrp) {
        final String mUlr = Constants.PROGRAM_LIST;
        System.out.println("***VrListActivity *** getNetData()" + mUlr);
        L.e("播放路径 mUrl=" + mUlr);
        RequestParams params = new RequestParams();
        System.out.println("***VrListActivity *** getNetData()" + params);
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("page_size", String.valueOf(8));
        System.out.println("***VrListActivity *** getNetData()" + channel_id);
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showOrHideLoadingBar(false);
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result) || responseInfo.result.length() < 5) {
//                    showNoDataDialog("已经是最后一页了");
                    System.out.println("---最后一页");
                    return;
                } else {
                    List<ChannelList> tmpList;
                    tmpList = new Gson().fromJson(responseInfo.result, new TypeToken<List<ChannelList>>() {
                    }.getType());
//                    GalleryItemView itemView1 = new GalleryItemView(LandscapeActivity_Second.this, tmpList);
//                    GalleryItemView itemView2 = new GalleryItemView(LandscapeActivity_Second.this, tmpList);
//                    itemView1.setHandler(mhandler);
//                    itemView2.setHandler(mhandler);
//                    views1.add(itemView1);
//                    views2.add(itemView2);
//                    adapter1.notifyDataSetChanged();
//                    adapter2.notifyDataSetChanged();
//                    System.out.println("---页数" + views1.size());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showOrHideLoadingBar(false);
                showNoInternetDialog(true);
            }
        });
    }

    //获取播放地址
    public void getplayUrl(final String vid, String imgurl) {
        final String img = imgurl;
        final String id = vid;
        String mUrl = Constants.PLAY_URL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("vid", vid);
        params.addBodyParameter("package", BaseApplication.packagename);
        params.addBodyParameter("app_version", BaseApplication.version);
        params.addBodyParameter("device", BaseApplication.device);
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();

                L.e("DetailActivity onStart ");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("---DetailActivity responseInfo:" + responseInfo.result);

                Play play = new Gson().fromJson(responseInfo.result, Play.class);
                play.setImgurl(img);
                String play_url = null;
                if (!TextUtils.isEmpty(play.getHd_url())) {
                    play_url = play.getHd_url();
                } else if (!TextUtils.isEmpty(play.getSd_url())) {
                    play_url = play.getSd_url();
                } else if (!TextUtils.isEmpty(play.getUhd_url())) {
                    play_url = play.getUhd_url();
                }
                startDownLoad(play_url, play, id);
                showOrHideLoadingBar(false);
                System.out.println("---play_url:" + play_url);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
                //show出来，网络异常，下载失败
                showOrHideLoadingBar(false);
                showNoDataDialog("网络异常，下载失败");
                finish();
            }
        });
    }

    public void startDownLoad(String play_url, Play play, String vid) {
        DbUtils db = DbUtils.create(this);
        LocalBean localBean = new LocalBean();
        localBean.setTitle(play.getTitle());
        localBean.setImage(play.getImgurl());
        localBean.setId(vid);
        localBean.setUrl(play_url);
        localBean.setCurState(0);//還沒下載，準備下載
        try {
            db.delete(localBean);
            db.save(localBean);
        } catch (DbException e) {
            e.printStackTrace();
        }
        BaseApplication.downLoadManager.addTask(vid, play_url, play.getTitle() + ".mp4", BaseApplication.VedioCacheUrl + play.getTitle() + ".mp4");
        System.out.println("---尺寸1："+views1.get(nowPage).getLocalUrlList().size());
        localUrlList.add(vid);
        System.out.println("---尺寸2："+views1.get(nowPage).getLocalUrlList().size());
        views1.get(nowPage).setText(views1.get(nowPage).getSelectedItemPosition());
        views2.get(nowPage).setText(views1.get(nowPage).getSelectedItemPosition());
    }
}
