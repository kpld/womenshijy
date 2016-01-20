package com.hotcast.vr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.Play;
import com.hotcast.vr.bean.Relation;
import com.hotcast.vr.bean.Urls;
import com.hotcast.vr.bean.Videos;
import com.hotcast.vr.receiver.DownloadReceiver;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 15/11/28.
 */
public class DetailActivity extends BaseActivity {
    @InjectView(R.id.moviename)
    TextView movie_name;
    @InjectView(R.id.tv_datetime)
    TextView tv_datetime;
    @InjectView(R.id.tv_movietime)
    TextView movietime;
    @InjectView(R.id.movie_introduced)
    TextView introduced;
    @InjectView(R.id.tv_cache)
    TextView tv_cache;
    @InjectView(R.id.translucentview)
    View translucentview;
    @InjectView(R.id.rl_movieimg)
    RelativeLayout rl_movieimg;
    @InjectView(R.id.ll_correlation)
    LinearLayout ll_correlation;
    @InjectView(R.id.ll_download)
    LinearLayout ll_download;

    private Play play;
    private Details details;
    //    可以播放的URL集合大小
    private int size;
    private BitmapUtils bitmapUtils;
    //    请求播放的URL
    private String mUrl;
    //    播放的URL
    private List<Urls> playUrl = new ArrayList<>();
    //    视频id集合
    private List<String> video_ids = new ArrayList<>();
    //    播放视频的标题
    private List<String> playTitle = new ArrayList<>();



    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    ViewHolder holder;
    Intent intent;

    @OnClick({R.id.back, R.id.play, R.id.ll_share})
    void clickType(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.play:
                L.e("你点击了播放的按钮");
//                if (!TextUtils.isEmpty(play_url)) {
                intent = new Intent(DetailActivity.this, PlayerVRActivityNew.class);
                intent.putExtra("play_url", play_url);
                intent.putExtra("title", title);
                intent.putExtra("splite_screen", false);
                DetailActivity.this.startActivity(intent);
//                }else {
//                    System.out.println("***DetailAcrtivity 播放路径为空无法播放");
//                }
                break;
//            case R.id.ll_download:
//                this.showDialog(null, "是否下载影片?", null, null, new BaseActivity.OnAlertSureClickListener() {
//                    @Override
//                    public void onclick() {
//                        Intent intent = new Intent(START);
////                        intent.putExtra("Details",details);
////                        intent.putExtra("play_url",play_url);
//                        BaseApplication.detailsList.add(details);
//                        BaseApplication.playUrls.add(play_url);
//                        DetailActivity.this.sendBroadcast(intent);
//                        showToast("已经加入下载列表");
//                        tv_cache.setText("已缓存");
//                        ll_download.setFocusable(false);
//                        DbUtils db = DbUtils.create(DetailActivity.this);
//                        LocalBean localBean = new LocalBean();
//                        localBean.setTitle(title);
//                        localBean.setImage(details.getImage());
//                        localBean.setId(media_id);
//                        localBean.setUrl(play_url);
//                        localBean.setCurState(0);//還沒下載，準備下載
////                        localBean.setLocalurl(localUrl);
//                        try {
//                            db.delete(localBean);
//                            db.save(localBean);
//                        } catch (DbException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//                if ("one".equals(action)) {
//
//                } else {
//                    translucentview.setVisibility(View.VISIBLE);
//                    final View popupView = View.inflate(DetailActivity.this,R.layout.layout_popup,null);
//                    popupGrid = (GridView) popupView.findViewById(R.id.popup_gd);
//                    ImageView iv_poor = (ImageView) popupView.findViewById(R.id.iv_poor);
//                    Button bt_catcheAll = (Button) popupView.findViewById(R.id.bt_catcheAll);
//                    Button bt_check = (Button) popupView.findViewById(R.id.bt_check);
//                    if (popupWindow == null){
//                        popupWindow = new PopupWindow(popupView,-2,-2);
//                    }else {
//                        int[] location = new int[2];
////                        view.getLocationInWindow(location);
//                        int dip = 60;
//                        int px = DensityUtil.dip2px(getApplicationContext(), dip);
//                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        popupWindow.showAtLocation((View) popupView.getParent(), Gravity.LEFT+Gravity.TOP,px,location[1]);
//                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
//                                1.0f, Animation.RELATIVE_TO_SELF, 0,
//                                Animation.RELATIVE_TO_SELF, 0.5f);
//                        sa.setDuration(200);
//                        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
//                        aa.setDuration(200);
//                        AnimationSet set = new AnimationSet(false);
//                        set.addAnimation(aa);
//                        set.addAnimation(sa);
//                        popupView.startAnimation(set);
//                    }
//                    iv_poor.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            translucentview.setVisibility(View.GONE);
//                            popupWindow.dismiss();
//                        }
//                    });
//                    bt_catcheAll.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
//                    bt_check.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(DetailActivity.this,List.class);
//                            DetailActivity.this.startActivity(intent);
//
//                        }
//                    });
//
//                    popupGrid.setAdapter(new BaseAdapter() {
//                        @Override
//                        public int getCount() {
//                            return videoses.size();
//                        }
//
//                        @Override
//                        public Object getItem(int position) {
//                            return videoses.get(position);
//                        }
//
//                        @Override
//                        public long getItemId(int position) {
//                            return position;
//                        }
//
//                        @Override
//                        public View getView(int position, View convertView, ViewGroup parent) {
//
//                            if (convertView == null){
//                                holder = new ViewHolder();
//                                convertView = View.inflate(DetailActivity.this,R.layout.popup_item,null);
//                                holder.bt_number = (Button) convertView.findViewById(R.id.bt_number);
//                                convertView.setTag(holder);
//                            }else{
//                                holder = (ViewHolder)convertView.getTag();
//                            }
//                            holder.bt_number.setText(videoses.get(position).getVideo_id());
//                            return convertView;
//                        }
//                    });
//                    popupGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            holder.bt_number.setBackgroundResource(R.mipmap.huancun);
//                            new Thread() {
//                                @Override
//                                public void run() {
//                                    downloadMedia(play_url);
//                                    super.run();
//                                }
//                            }.start();
//                        }
//                    });
//                }

//                break;
            case R.id.ll_share:
//                TODO 弹出一个框放第三方的图标
                break;
        }
    }

    class ViewHolder {
        Button bt_number;
    }

    String play_url;
    String title;
    String media_id;

    public void getplayUrl() {
        mUrl = Constants.URL_PLAY;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        if (video_ids.size() > 0) {
            media_id = video_ids.get(0);
            params.addBodyParameter("media_id", media_id);
            L.e("DetailActivity media_id = " + video_ids.get(0));
        } else {
            finish();
        }
        this.httpPost(mUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                L.e("DetailActivity onStart ");
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("---DetailActivity responseInfo:" + responseInfo.result);

                List<Play> playUrls = new Gson().fromJson(responseInfo.result, new TypeToken<List<Play>>() {
                }.getType());
                size = playUrls.size();
                L.e("DetailActivity playUrls:" + playUrls + "***size = " + size);

                for (int i = 0; i < size; i++) {
                    playUrl.add(playUrls.get(i).getUrls());
                    playTitle.add(playUrls.get(i).getTitle());
                }
                play_url = playUrl.get(0).getShd();
                System.out.println("***play_url:" + play_url);
                title = playTitle.get(0);
              initCatch(play_url);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                L.e("DetailActivity onFailure ");
            }
        });


    }
    //初始化下载按钮
    private void initCatch(String play_url) {
        DbUtils db = DbUtils.create(DetailActivity.this);
        boolean isdownLoad = false;
        try {
            List<LocalBean> list = db.findAll(LocalBean.class);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (!TextUtils.isEmpty(play_url)) {
                        System.out.println("***play_url:" + play_url + "***list.get(i).getUrl():" + list.get(i).getUrl());
                        isdownLoad = play_url.equals(list.get(i).getUrl());
                    }
                }
                if (isdownLoad) {
                    BaseApplication.isDownLoad = true;
                    tv_cache.setText("已缓存");
                    ll_download.setFocusable(false);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private String action;
    private String resource;
    private String requestUrl;
    DownloadReceiver receiver;
    IntentFilter filter;

    @Override
    public int getLayoutId() {
        return R.layout.movie_detail;
    }

    @Override
    public void init() {
        requestUrl = Constants.URL_DETAIL;
        receiver = new DownloadReceiver();
        filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        getNetDate();
        ll_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BaseApplication.isDownLoad) {
                    DetailActivity.this.showDialog(null, "是否下载影片?", null, null, new OnAlertSureClickListener() {
                        @Override
                        public void onclick() {
                            Intent intent = new Intent(START);
//                        intent.putExtra("Details",details);
//                        intent.putExtra("play_url",play_url);
                            BaseApplication.detailsList.add(details);
                            BaseApplication.playUrls.add(play_url);
                            DetailActivity.this.sendBroadcast(intent);
                            showToast("已经加入下载列表");
                            tv_cache.setText("已缓存");
                            BaseApplication.isDownLoad = true;
                            ll_download.setFocusable(false);
                            DbUtils db = DbUtils.create(DetailActivity.this);
                            LocalBean localBean = new LocalBean();
                            localBean.setTitle(title);
                            localBean.setImage(details.getImage());
                            localBean.setId(media_id);
                            localBean.setUrl(play_url);
                            localBean.setCurState(0);//還沒下載，準備下載
//                          localBean.setLocalurl(localUrl);
                            try {
                                db.delete(localBean);
                                db.save(localBean);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }else {
                    DetailActivity.this.showToast("亲，您已经缓存了");
                }
            }
        });
    }


    private void initView() {
        L.e("***填充数据***" + details.getVideo_length());
        bitmapUtils = new BitmapUtils(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        img_movie.setLayoutParams(params);
//        bitmapUtils.
        if (details.getImage() != null) {
            bitmapUtils.display(rl_movieimg, details.getImage());
        }
        if (details.getTitle() != null) {
            movie_name.setText(details.getTitle());
        }
        if (details.getVideo_length() != null) {
            movietime.setText("片长：" + details.getVideo_length());
        }
        if (details.getUpdate_at() != null) {
           long datetime = Long.parseLong(details.getUpdate_at())*1000l;
            System.out.println("***datetime = "+new Date(datetime));
            String date = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(datetime));
//            Date d = new Date(Integer.parseInt(details.getUpdate_at()));
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
////            sf.format(d);
//            tv_datetime.setText("更新时间：" + sf.format(d));
            tv_datetime.setText("更新时间：" + date);
        }
        introduced.setText(details.getDesc());
        System.out.println("---" + details.getUpdate_at() + "**" + details.getVideo_length() + "**" + Integer.parseInt(details.getUpdate_at()));
//        Integer.parseInt(details.getUpdate_at());


    }

    private void getNetDate() {
        L.e("DetailActivity getNetDate()");
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("resource", resource);
        params.addBodyParameter("action", action);
        this.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("****getNateDate *** DetailActivity responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    ArrayList<Videos> videoses;

    private void setViewData(String json) {
        L.e("DetailActivity setViewData()");
        if (Utils.textIsNull(json)) {
            finish();
            return;
        }

        details = new Gson().fromJson(json, Details.class);
//        size = detailses.size();
//        Details details = new Gson().fromJson(json,new );
        videoses = details.getVideo();
        L.e("DetailActivity details.getVideo() =" + details.getVideo());
        L.e("DetailActivity videoses =" + videoses);
        if (videoses != null) {
            for (int i = 0; i < videoses.size(); i++) {
                video_ids.add(videoses.get(i).getVideo_id());
            }
            L.e("DetailActivity detailses = " + details + videoses.size());
            L.e("DetailActivity video_ids = " + video_ids);
            initView();
        } else {
            finish();
        }
        getplayUrl();
        getRelationDate();
    }

    @Override
    public void getIntentData(Intent intent) {
        action = intent.getStringExtra("action");
        resource = intent.getStringExtra("resource");
        L.e("DetailActivity responseInfo:" + action + "***" + resource);
    }

    List<Relation> relations;

    private void getRelationDate() {
        String url = Constants.RELATION;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("vid", video_ids.get(0));
        System.out.println("***DetailActivity***getRelationDate() vid = " + video_ids.get(0));
        this.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.e("****DetailActivity responseInfo:" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                try {
                    relations = new Gson().fromJson(responseInfo.result, new TypeToken<List<Relation>>() {
                    }.getType());
                } catch (IllegalStateException e) {
                    DetailActivity.this.showToast("解析出现错误，请刷新数据");
                    finish();
                }

                setItemMovies(DetailActivity.this, relations);

            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    public void setItemMovies(final Context context, List<Relation> relations) {
        ll_correlation.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 30, 0);
        for (int i = 0; i < relations.size(); i++) {
            final Relation relation = relations.get(i);
//            System.out.println("---ItemView  roll---" + relation);

            LinearLayout contentView = (LinearLayout) View.inflate(context,
                    R.layout.item_item_img, null);
            ImageView iv_movie = (ImageView) contentView
                    .findViewById(R.id.iv_movie);
            BitmapUtils bitmapUtils = new BitmapUtils(DetailActivity.this);

            if (relation.getPosturl() != null) {
                bitmapUtils.display(iv_movie, relation.getPosturl());
            }
            TextView tv_movie = (TextView) contentView
                    .findViewById(R.id.tv_movie);
            tv_movie.setText(relation.getVname());
            iv_movie.setFocusable(true);
            iv_movie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlayerVRActivityNew.class);
                    intent.putExtra("play_url", relation.getHd_url());
                    intent.putExtra("title", relation.getVname());
                    intent.putExtra("splite_screen", false);
                    MobclickAgent.onEvent(DetailActivity.this,"play_start");
                    context.startActivity(intent);
//                    System.out.println("---ItemView 条目被点击了---");
                }
            });
            contentView.setLayoutParams(params);
            ll_correlation.addView(contentView);

        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
