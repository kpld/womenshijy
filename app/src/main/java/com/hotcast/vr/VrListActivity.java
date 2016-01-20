package com.hotcast.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.imageView.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.tools.L;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by joey on 8/10/15.
 */
public class VrListActivity extends BaseActivity {
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;
    private VrListView view1, view2;
    private String requestUrl;
    private int type;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descs = new ArrayList<>();
    Spannable span;
    String page;
    /**
     * 控件宽度
     */
    public static int mWidth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    //    private int size;
    Image3DSwitchView img3D;
    Image3DSwitchView img3D2;
    TextView tv_page1;
    TextView tv_page2;
    TextView tv_title1;
    TextView tv_title2;
    TextView tv_desc1;
    TextView tv_desc2;
    TextView bt_ceach1;
    TextView bt_ceach2;
    int index = 1;
    List<LocalBean> dbList = null;
    List<String> localUrlList;
    DbUtils db;
    int mCurrentImg;

    @Override
    public void init() {
        System.out.println("***VrListActivity *** init()" + channel_id);
        view1 = new VrListView(this);
        view2 = new VrListView(this);
        db = DbUtils.create(VrListActivity.this);
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
                localUrlList.add(localBean.getUrl());
            }
        }
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img3D = (Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        tv_page1 = (TextView) view1.getRootView().findViewById(R.id.tv_page);
        tv_page2 = (TextView) view2.getRootView().findViewById(R.id.tv_page);

        tv_title1 = (TextView) view1.getRootView().findViewById(R.id.tv_title);
        tv_title2 = (TextView) view2.getRootView().findViewById(R.id.tv_title);
        tv_desc1 = (TextView) view1.getRootView().findViewById(R.id.tv_desc);
        tv_desc2 = (TextView) view2.getRootView().findViewById(R.id.tv_desc);

        bt_ceach1 = (TextView) view1.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach1.setOnClickListener(this);
        bt_ceach2 = (TextView) view2.getRootView().findViewById(R.id.bt_ceach);
        bt_ceach2.setOnClickListener(this);
//        homeRolls.addAll(homeRolls);
        mWidth = img3D.getMeasuredWidth();
        // 每张图片的宽度设定为控件宽度的百分之六十
        mImageWidth = (int) (mWidth * 0.6);

        System.out.println("****vrPlays = " + vrPlays);
        page = index + "/" + vrPlays.size();
        span = new SpannableString(page);
        span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_page1.setText(span);
        tv_page2.setText(span);
        System.out.println("*** VrListActivity***title = " + titles.get(index));
        System.out.println("*** VrListActivity***desc = " + descs.get(index));
        for (int i = 0; i < vrPlays.size(); i++) {
//            System.out.println(i+"---电影名称："+vrPlays.get(i).getTitle());
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage());
            image3DView.setLayoutParams(params);
            final String url = vrPlays.get(i).getVideo_url();
            final String title = vrPlays.get(i).getTitle();
            //for jiuyou change
//            image3DView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    intent = new Intent(VrListActivity.this, PlayerVRActivityNew.class);
//                    intent.putExtra("play_url", url);
//                    intent.putExtra("title", title);
//                    intent.putExtra("splite_screen", true);
//                    VrListActivity.this.startActivity(intent);
//                    System.out.println("***你点击了item，准备播放**");
//                }
//            });

            img3D.addView(image3DView);
        }
        for (int i = 0; i < vrPlays.size(); i++) {
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            bitmapUtils.display(image3DView, vrPlays.get(i).getImage());
            image3DView.setLayoutParams(params);
            final String url = vrPlays.get(i).getVideo_url();
            final String title = vrPlays.get(i).getTitle();
//            image3DView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    play(url, title);
//                    System.out.println("***你点击了item，准备播放**");
//                }
//            });

            img3D2.addView(image3DView);
        }
        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = titles.size() - 1;
        }
        tv_title1.setText(titles.get(mCurrentImg));
        tv_title2.setText(titles.get(mCurrentImg));
        tv_desc1.setText(descs.get(mCurrentImg));
        tv_desc2.setText(descs.get(mCurrentImg));
        if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
            bt_ceach2.setText("已下载");
            bt_ceach1.setText("已下载");
        }
        img3D.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
//                if (index <= vrPlays.size()) {
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                } else {
//                    index = 1;
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                }
//                if (localUrlList.contains(vrPlays.get(index - 1).getVideo_url())) {
//                    bt_ceach2.setText("已下载");
//                    bt_ceach1.setText("已下载");
//                }

            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D2.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D2.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);

                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                }
                System.out.println("-----mcurrentIndex1：" + img3D.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D2.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
//                if (index > 0) {
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                } else {
//                    index = vrPlays.size();
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                }
//                if (localUrlList.contains(vrPlays.get(index - 1).getVideo_url())) {
//                    bt_ceach2.setText("已下载");
//                    bt_ceach1.setText("已下载");
//                }
            }
        });
        img3D2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();
//                if (index <= vrPlays.size()) {
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                } else {
//                    index = 1;
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                }
//                if (localUrlList.contains(vrPlays.get(index - 1).getVideo_url())) {
//                    bt_ceach2.setText("已下载");
//                    bt_ceach1.setText("已下载");
//                }

            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                } else {
                    index = 1;
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (index >= 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                } else {
                    index = vrPlays.size();
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(mCurrentImg));
                    tv_title2.setText(titles.get(mCurrentImg));
                    tv_desc1.setText(descs.get(mCurrentImg));
                    tv_desc2.setText(descs.get(mCurrentImg));
                    if (localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                        bt_ceach2.setText("已下载");
                        bt_ceach1.setText("已下载");
                    } else {
                        bt_ceach2.setText("未下载");
                        bt_ceach1.setText("未下载");
                    }
                }
                System.out.println("-----mcurrentIndex2：" + img3D2.getImgIndex());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
//                if (index > 0) {
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                } else {
//                    index = vrPlays.size();
//                    page = index + "/" + vrPlays.size();
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(titles.get(index-1));
//                    tv_title2.setText(titles.get(index-1));
//                    tv_desc1.setText(descs.get(index-1));
//                    tv_desc2.setText(descs.get(index-1));
//                }
//                if (localUrlList.contains(vrPlays.get(index - 1).getVideo_url())) {
//                    bt_ceach2.setText("已下载");
//                    bt_ceach1.setText("已下载");
//                }
            }
        });
        img3D.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
            @Override
            public void onImageSwitch(int currentImage) {
                System.out.println("---是否触发监听");
//                    img3D2.setCurrentImage(currentImage);
            }
        });
        img3D2.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
            @Override
            public void onImageSwitch(int currentImage) {
                System.out.println("---是否触发监听");
//                img3D.setCurrentImage(currentImage);
            }
        });

        container1.removeAllViews();
        container2.removeAllViews();
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        container1.setOnClickListener(this);
//        container2.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
//        getIntentData(getIntent());
        super.onStart();
    }

    String channel_id;
    List<VrPlay> vrPlays;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ceach:
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = titles.size() - 1;
                }
                if (!localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
                    System.out.println("---点击了：" + vrPlays.get(mCurrentImg).getTitle() + "--url:" + vrPlays.get(mCurrentImg).getVideo_url());
//                    System.out.println("---下载：" + vrPlays.get(mCurrentImg).getTitle());
                    Intent intent = new Intent(START);
                    Details details = new Details();
                    details.setTitle(vrPlays.get(mCurrentImg).getTitle());
                    System.out.println("---开始下载" + vrPlays.get(mCurrentImg).getTitle());
                    details.setImage(vrPlays.get(mCurrentImg).getImage());
                    details.setDesc(vrPlays.get(mCurrentImg).getDesc());
//
                    BaseApplication.detailsList.add(details);
                    BaseApplication.playUrls.add(vrPlays.get(mCurrentImg).getVideo_url());
                    VrListActivity.this.sendBroadcast(intent);
                    BaseApplication.isDownLoad = true;
                    DbUtils db = DbUtils.create(VrListActivity.this);
                    LocalBean localBean = new LocalBean();
                    localBean.setTitle(details.getTitle());
                    localBean.setImage(details.getImage());
                    localBean.setId(vrPlays.get(mCurrentImg).getVideo_url());
                    localBean.setUrl(vrPlays.get(mCurrentImg).getVideo_url());
                    localBean.setCurState(0);//還沒下載，準備下載
                    try {
                        db.delete(localBean);
                        db.save(localBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    localUrlList.add(vrPlays.get(mCurrentImg).getVideo_url());
                    bt_ceach2.setText("已下载");
                    bt_ceach1.setText("已下载");
                }
                break;
//            case R.id.container1:
//            case R.id.container2:
//                downLoadMovie();
//                break;
        }
    }

    public void downLoadMovie() {
        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = titles.size() - 1;
        }
        if (!localUrlList.contains(vrPlays.get(mCurrentImg).getVideo_url())) {
            System.out.println("---点击了：" + vrPlays.get(mCurrentImg).getTitle() + "--url:" + vrPlays.get(mCurrentImg).getVideo_url());
//                    System.out.println("---下载：" + vrPlays.get(mCurrentImg).getTitle());
            Intent intent = new Intent(START);
            Details details = new Details();
            details.setTitle(vrPlays.get(mCurrentImg).getTitle());
            System.out.println("---开始下载" + vrPlays.get(mCurrentImg).getTitle());
            details.setImage(vrPlays.get(mCurrentImg).getImage());
            details.setDesc(vrPlays.get(mCurrentImg).getDesc());
//
            BaseApplication.detailsList.add(details);
            BaseApplication.playUrls.add(vrPlays.get(mCurrentImg).getVideo_url());
            VrListActivity.this.sendBroadcast(intent);
            BaseApplication.isDownLoad = true;
            DbUtils db = DbUtils.create(VrListActivity.this);
            LocalBean localBean = new LocalBean();
            localBean.setTitle(details.getTitle());
            localBean.setImage(details.getImage());
            localBean.setId(vrPlays.get(mCurrentImg).getVideo_url());
            localBean.setUrl(vrPlays.get(mCurrentImg).getVideo_url());
            localBean.setCurState(0);//還沒下載，準備下載
            try {
                db.delete(localBean);
                db.save(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
            localUrlList.add(vrPlays.get(mCurrentImg).getVideo_url());
            bt_ceach2.setText("已下载");
            bt_ceach1.setText("已下载");
        }
    }

    @Override
    public void getIntentData(Intent intent) {
        channel_id = intent.getStringExtra("channel_id");
        vrPlays = (List<VrPlay>) getIntent().getSerializableExtra("vrPlays");
        for (int i = 0; i < vrPlays.size(); i++) {
            VrPlay vrPlay = vrPlays.get(i);
            titles.add(vrPlay.getTitle());
            descs.add(vrPlay.getDesc());
        }
        System.out.println("*** VrListActivity ***vrPlays = " + vrPlays + "titles = " + titles + "descs = " + descs);
        System.out.println("-----titlessssss:" + titles.size());
        type = intent.getIntExtra("type", 0);

    }

    Intent intent;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("---keyCode = " + keyCode);
        switch (keyCode) {

            case KeyEvent.KEYCODE_DPAD_LEFT:
                ++index;
                if (index <= vrPlays.size()) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(index));
                    tv_title2.setText(titles.get(index));
                    tv_desc1.setText(descs.get(index));
                    tv_desc2.setText(descs.get(index));
                } else {
                    index = 1;
                }
                img3D.scrollToNext();
                img3D2.scrollToNext();
                L.e("你点击了下一张");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                --index;
                if (index > 0) {
                    page = index + "/" + vrPlays.size();
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(VrListActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + vrPlays.size()).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(titles.get(index));
                    tv_title2.setText(titles.get(index));
                    tv_desc1.setText(descs.get(index));
                    tv_desc2.setText(descs.get(index));
                } else {
                    index = vrPlays.size();
                }
                img3D.scrollToPrevious();
                img3D2.scrollToPrevious();
                L.e("你点击了上一张");
                break;

            case KeyEvent.KEYCODE_ENTER:
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                for (int i = 0; i < vrPlays.size(); i++) {
                    if (i == index) {
                        String url = vrPlays.get(i).getVideo_url();
                        String title = vrPlays.get(i).getTitle();
                        play(url, title);
                    }
                }
                L.e("你点击了进入播放页");
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_BUTTON_B:
                finish();
                break;
        }
        return true;
    }

    private void play(String url, String title) {
        intent = new Intent(VrListActivity.this, PlayerVRActivityNew.class);
        intent.putExtra("play_url", url);
        intent.putExtra("title", title);
        intent.putExtra("splite_screen", true);
        VrListActivity.this.startActivity(intent);
        System.out.println("***你点击了item，准备播放**");
    }


    /**
     * 记录上次触摸的横坐标值
     */
    private float mLastMotionX;
    /**
     * 滚动到下一张图片的速度
     */
    private static final int SNAP_VELOCITY = 600;
    private VelocityTracker mVelocityTracker;
    public Image3DSwitchView.OnMovechangeListener changeLisener;


    int downX, downY;
    int upX, upY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                upX = (int) event.getX();
                upY = (int) event.getY();
                break;
        }
        int xlen = Math.abs(downX - upX);
        int ylen = Math.abs(downY - upY);
        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
        if (length < 8) {
            //执行点击事件
            downLoadMovie();
            return true;
        }
        if (img3D.getmScroller().isFinished()) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            int action = event.getAction();
            float x = event.getX();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    changeLisener = img3D.getChangeLisener();
                    // 记录按下时的横坐标
                    mLastMotionX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int disX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    // 当发生移动时刷新图片的显示状态
                    img3D.scrollBy(disX, 0);
                    img3D.refreshImageShowing();
                    if (changeLisener != null) {
                        changeLisener.OnMovechange(disX);

                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) mVelocityTracker.getXVelocity();
                    if (shouldScrollToNext(velocityX)) {
                        // 滚动到下一张图
                        img3D.scrollToNext();
                        if (changeLisener != null) {
                            changeLisener.Next();

                        }
                    } else if (shouldScrollToPrevious(velocityX)) {
                        // 滚动到上一张图
                        img3D.scrollToPrevious();
                        if (changeLisener != null) {
                            changeLisener.Previous();
                        }
                    } else {
                        // 滚动回当前图片
                        img3D.scrollBack();
                        if (changeLisener != null) {
                            changeLisener.Back();

                        }
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * 记录每张图片的宽度
     */
    private int mImageWidth;

    /**
     * 判断是否应该滚动到上一张图片。
     */
    private boolean shouldScrollToPrevious(int velocityX) {
        return velocityX > SNAP_VELOCITY || img3D.getScrollX() < -mImageWidth / 2;
    }

    /**
     * 判断是否应该滚动到下一张图片。
     */
    private boolean shouldScrollToNext(int velocityX) {
        return velocityX < -SNAP_VELOCITY || img3D.getScrollX() > mImageWidth / 2;
    }

}
