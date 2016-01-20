package com.hotcast.vr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.imageView.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.LocalListView;
import com.hotcast.vr.pageview.RefreshListView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class LocalCachelActivity extends BaseActivity {
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;

    private LocalListView view1, view2;
    private Image3DSwitchView img3D, img3D2;
    private String requestUrl;
    private int type;
    Spannable span;
    String page;
    TextView tv_page1;
    TextView tv_page2;
    TextView tv_title1;
    TextView tv_title2;
    TextView tv_desc1;
    TextView tv_desc2;
    TextView tv_pecent;
    TextView tv_speed;
    int index = 1;
    /**
     * 控件宽度
     */
    public static int mWidth;
    //缓存的视频名称（不包含前缀地址）
    private List<LocalBean> dbList;
    DbUtils db;
    BitmapUtils bitmapUtils;
    private DetailReceiver receiver;
    //db原始尺寸
    private int trueSize;
    private List<LocalBean> Truelist;
    int mCurrentImg;
    Button bt_delete1;
    Button bt_delete2;
    Button bt_zanting1;
    Button bt_zanting2;
    ImageView cache_no1;
    ImageView cache_no2;
    LinearLayout ivBack1;
    LinearLayout ivBack2;
    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    @Override
    public void init() {
        receiver = new DetailReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        speeds = new HashMap<>();
        db = DbUtils.create(this);
        workForList();
    }

    public void workForList() {
        index = 1;
        if (dbList == null) {
            dbList = new ArrayList<>();
        } else {
            dbList.clear();
        }
        trueSize = Truelist.size();
        if (Truelist.size() > 0 && Truelist.size() < 5) {
            addSelfToFive();
        }else{
            dbList.addAll(Truelist);
        }
        BaseApplication.size = dbList == null ? 0 : dbList.size();
    }

    public void addSelfToFive() {
        dbList.addAll(Truelist);
        if (dbList.size() < 5) {
            addSelfToFive();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        view1 = new LocalListView(this);
        view2 = new LocalListView(this);
        cache_no1 = (ImageView) findViewById(R.id.cache_no1);
        cache_no2 = (ImageView) findViewById(R.id.cache_no2);
        ivBack1 = (LinearLayout) findViewById(R.id.ivBack1);
        ivBack1.setOnClickListener(this);
        ivBack2 = (LinearLayout) findViewById(R.id.ivBack2);
        ivBack1.setOnClickListener(this);
        if (dbList.size() <= 0) {
            System.out.println("---数据库没有数据");
//            view1.hideOrShowCache_no(true);
//            view2.hideOrShowCache_no(true);
            hideCache(true);
        } else {
            //显示逻辑
//            view1.hideOrShowCache_no(false);
//            view2.hideOrShowCache_no(false);
            System.out.println("---数据库：" + dbList.size());
            init3DView();
        }
    }

    public void init3DView() {
        bitmapUtils = new BitmapUtils(this);
        img3D = (Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        tv_page1 = (TextView) view1.getRootView().findViewById(R.id.tv_page);
        tv_page2 = (TextView) view2.getRootView().findViewById(R.id.tv_page);

        tv_title1 = (TextView) view1.getRootView().findViewById(R.id.tv_title);
        tv_title2 = (TextView) view2.getRootView().findViewById(R.id.tv_title);
        bt_delete1 = (Button) view1.getRootView().findViewById(R.id.bt_delete);
        bt_delete1.setOnClickListener(this);
        bt_delete2 = (Button) view2.getRootView().findViewById(R.id.bt_delete);
        bt_delete2.setOnClickListener(this);

        bt_zanting1 = (Button) view1.getRootView().findViewById(R.id.bt_zanting);
        bt_zanting1.setOnClickListener(this);
        bt_zanting2 = (Button) view2.getRootView().findViewById(R.id.bt_zanting);
        bt_zanting2.setOnClickListener(this);

//        tv_desc1 = (TextView) view1.getRootView().findViewById(R.id.tv_desc);
//        tv_desc2 = (TextView) view2.getRootView().findViewById(R.id.tv_desc);
        initPagerView();
        allChange();
    }

    public void initPagerView() {
        img3D.removeAllViews();
        img3D2.removeAllViews();
//        ((ViewGroup) view1.getRootView().getParent()).removeView(view1.getRootView());
        container1.removeView(view1.getRootView());
        container2.removeView(view2.getRootView());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        page = index + "/" + trueSize;
        span = new SpannableString(page);
        span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_page1.setText(span);
        tv_page2.setText(span);
        for (int i = 0; i < dbList.size(); i++) {
            final int index = i;
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imgurl = dbList.get(i).getImage();
            bitmapUtils.display(image3DView, imgurl);
            image3DView.setLayoutParams(params);
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String localurl = dbList.get(index).getLocalurl();
                    System.out.print("---点击播放了：" + localurl + "state:" + dbList.get(index).getCurState());
                    clickVedio(index);
                }
            });
            img3D.addView(image3DView);
        }
        for (int i = 0; i < dbList.size(); i++) {
            final int index = i;
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imgurl = dbList.get(i).getImage();
            bitmapUtils.display(image3DView, imgurl);
            image3DView.setLayoutParams(params);
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String localurl = dbList.get(index).getLocalurl();
                    System.out.print("---点击播放了：" + localurl + "state:" + dbList.get(index).getCurState());
                    clickVedio(index);
                }
            });
            img3D2.addView(image3DView);
        }
        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = dbList.size() - 1;
        }
        tv_title1.setText(dbList.get(mCurrentImg).getTitle());
        tv_title2.setText(dbList.get(mCurrentImg).getTitle());
        if (dbList.get(mCurrentImg).getCurState() == 3) {
            view1.hideOrShowLoading(false);
            view2.hideOrShowLoading(false);
        } else {
            view1.hideOrShowLoading(true);
            view2.hideOrShowLoading(true);
        }
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_delete:
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = dbList.size() - 1;
                }
                System.out.println("---点击了删除");
                try {
                    if (dbList.get(mCurrentImg).getLocalurl() != null) {
                        db.delete(dbList.get(mCurrentImg));
                        delete(dbList.get(mCurrentImg).getLocalurl());
                        for (int i = 0; i < Truelist.size(); i++) {
                            if (Truelist.get(i).getLocalurl().equals(dbList.get(mCurrentImg).getLocalurl())) {
                                System.out.println("---删除：" + Truelist.get(i).getLocalurl());
                                Truelist.remove(i);
                                System.out.println("---真实1：" + Truelist.size());
                                workForList();
                                System.out.println("---真实2：" + Truelist.size());
                                view1 = new LocalListView(this);
                                view2 = new LocalListView(this);
                                if (dbList.size() <= 0) {
//                                    view1.hideOrShowCache_no(true);
//                                    view2.hideOrShowCache_no(true);
                                    hideCache(true);
                                } else {
                                    //显示逻辑
//                                    view1.hideOrShowCache_no(false);
//                                    view2.hideOrShowCache_no(false);
                                    hideCache(false);
                                    System.out.println("---点击后——dblist：" + dbList.size());
                                    init3DView();
                                }
                            }
                        }

                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_zanting:
//                Intent intent = new Intent(PAUSE);
//                intent.putExtra("index", mCurrentImg);
//                sendBroadcast(intent);
                break;
//            case R.id.ivBack2:
//            case R.id.ivBack1:
//                LocalCachelActivity.this.finish();
//                break;
//            case R.id.container1:
//            case R.id.container2:
//                mCurrentImg = img3D.getImgIndex() - 1;
//                if (mCurrentImg < 0) {
//                    mCurrentImg = dbList.size() - 1;
//                }
//                clickVedio(mCurrentImg);
//                break;
        }
    }

    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    public void clickVedio(int i) {
        String localurl = dbList.get(i).getLocalurl();
        System.out.print("---点击播放了：" + localurl + "state:" + dbList.get(i).getCurState());
        File file;
        if (localurl != null) {
            file = new File(localurl);
        } else {
            file = new File(" ");
        }
        if (localurl != null && file.exists()) {
//                        System.out.println("---本地地址：" + localurl + "---url:" + list.get(i).getUrl());
            Intent intent = new Intent(LocalCachelActivity.this, PlayerVRActivityNew.class);
            intent.putExtra("play_url", localurl);
            intent.putExtra("title", dbList.get(i).getTitle());
            intent.putExtra("splite_screen", false);
            LocalCachelActivity.this.startActivity(intent);
        } else if (localurl == null) {
            if (dbList.get(i).getCurState() == -1) {
                System.out.print("---重新下载");
                Intent intent = new Intent(START);
                LocalCachelActivity.this.sendBroadcast(intent);
                try {
                    db.delete(dbList.get(i));
                    dbList.get(i).setCurState(0);
                    db.save(dbList.get(i));
                } catch (DbException e) {
                    e.printStackTrace();
                }
            } else if (dbList.get(i).getCurState() == 0) {
                System.out.print("---暂停");
                try {
                    db.delete(dbList.get(i));
                    dbList.get(i).setCurState(-1);
                    db.save(dbList.get(i));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(PAUSE);
                intent.putExtra("index", i);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void getIntentData(Intent intent) {
        Truelist = (ArrayList<LocalBean>) getIntent().getSerializableExtra("dbList");
        System.out.println("---传递数据的尺寸2："+Truelist.size());
        if (Truelist == null) {
            Truelist = new ArrayList<>();
        }
    }

    public void allChange() {
        img3D.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
//                if (index <= trueSize) {
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
//                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
//                } else {
//                    index = 1;
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                }
//                if (dbList.get(index - 1).getCurState() == 3) {
//                    view1.hideOrShowLoading(false);
//                    view2.hideOrShowLoading(false);
//                } else {
//                    view1.hideOrShowLoading(true);
//                    view2.hideOrShowLoading(true);
//                }
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D2.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = dbList.size() - 1;
                }
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                }
                if (dbList.get(mCurrentImg).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
                System.out.println("---滑动：" + mCurrentImg + "--url:" + dbList.get(mCurrentImg).getUrl() + "--title:" + dbList.get(mCurrentImg).getTitle());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D2.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = dbList.size() - 1;
                }
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                }
                if (dbList.get(mCurrentImg).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
                System.out.println("---滑动：" + mCurrentImg + "--url:" + dbList.get(mCurrentImg).getUrl() + "--title:" + dbList.get(mCurrentImg).getTitle());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D2.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
//                if (index > 0) {
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                } else {
//                    index = trueSize;
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                }
//                if (dbList.get(index - 1).getCurState() == 3) {
//                    view1.hideOrShowLoading(false);
//                    view2.hideOrShowLoading(false);
//                } else {
//                    view1.hideOrShowLoading(true);
//                    view2.hideOrShowLoading(true);
//                }
            }
        });
        img3D2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();
//                if (index <= trueSize) {
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                } else {
//                    index = 1;
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                }
//                if (dbList.get(index - 1).getCurState() == 3) {
//                    view1.hideOrShowLoading(false);
//                    view2.hideOrShowLoading(false);
//                } else {
//                    view1.hideOrShowLoading(true);
//                    view2.hideOrShowLoading(true);
//                }
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D.scrollToNext();
                ++index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = dbList.size() - 1;
                }
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                }
                if (dbList.get(mCurrentImg).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
                System.out.println("---滑动：" + mCurrentImg + "--url:" + dbList.get(mCurrentImg).getUrl() + "--title:" + dbList.get(mCurrentImg).getTitle());
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D.scrollToPrevious();
                --index;
                mCurrentImg = img3D.getImgIndex() - 1;
                if (mCurrentImg < 0) {
                    mCurrentImg = dbList.size() - 1;
                }
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(mCurrentImg).getTitle());
                    tv_title2.setText(dbList.get(mCurrentImg).getTitle());
                }
                if (dbList.get(mCurrentImg).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
                System.out.println("---滑动：" + mCurrentImg + "--url:" + dbList.get(mCurrentImg).getUrl() + "--title:" + dbList.get(mCurrentImg).getTitle());
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
//                if (index > 0) {
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                } else {
//                    index = trueSize;
//                    page = index + "/" + trueSize;
//                    span = new SpannableString(page);
//                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
//                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_page1.setText(span);
//                    tv_page2.setText(span);
//                    tv_title1.setText(dbList.get(index - 1).getTitle());
//                    tv_title2.setText(dbList.get(index - 1).getTitle());
//                }
//                if (dbList.get(index - 1).getCurState() == 3) {
//                    view1.hideOrShowLoading(false);
//                    view2.hideOrShowLoading(false);
//                } else {
//                    view1.hideOrShowLoading(true);
//                    view2.hideOrShowLoading(true);
//                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    //刷新电影下载状况
                    RefreshDownLoading();
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = dbList.size() - 1;
                    }
                    break;
                case 101:
                    mCurrentImg = img3D.getImgIndex() - 1;
                    if (mCurrentImg < 0) {
                        mCurrentImg = dbList.size() - 1;
                    }
                    //刷新电影下载状况
                    if (dbList.get(mCurrentImg).getCurState() == 3) {
                        view1.hideOrShowLoading(false);
                        view2.hideOrShowLoading(false);
                    } else {
                        view1.hideOrShowLoading(true);
                        view2.hideOrShowLoading(true);
                    }
                    break;
            }
        }
    };
    Map<String, String> speeds;//下载速度的集合
    long pecent = 0;
    String action;

    public void RefreshDownLoading() {
        mCurrentImg = img3D.getImgIndex() - 1;
        if (mCurrentImg < 0) {
            mCurrentImg = dbList.size() - 1;
        }
        String indexSpeed = speeds.get(dbList.get(mCurrentImg).getUrl());
        if (dbList.get(mCurrentImg).getCurState() == 1 || indexSpeed != null) {
            view1.RefreshTextView(indexSpeed);
            view2.RefreshTextView(indexSpeed);
        }

        System.out.println("---RefreshDownLoading信息：" + indexSpeed + "--url:" + dbList.get(mCurrentImg).getUrl() + "--title:" + dbList.get(mCurrentImg).getTitle());
    }

    /**
     * 这是接受下载进度的广播接受者
     */
    class DetailReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String speed = "";
            action = intent.getAction();
            String play_url = intent.getStringExtra("play_url");//電影的下載地址，作為電影的唯一标识
            if ("DOWNLOADING".equals(action)) {
                //执行相应操作
                long total = intent.getLongExtra("total", -1);//电影总大小
                long current = intent.getLongExtra("current", -1);//电影当前进度
                if (pecent == 0) {
                    pecent = current;
                } else {
                    speed = (current - pecent) / 1024 + "KB/S" + " 已下载" + (current * 100) / total + "%";
                    pecent = current;
                    speeds.put(play_url, speed);
                    mHandler.sendEmptyMessage(100);
                }
                System.out.println("---接收到的信息：" + speed + "--url" + play_url);
//
            } else if ("FINISH".equals(action)) {
//                下載完畢，執行下載完畢的邏輯
                speeds.put(play_url, "FINISH");
                String localurl = intent.getStringExtra("localurl");
                System.out.println("----接受到的網絡地址:" + play_url);
                for (int i = 0; i < dbList.size(); i++) {
//                    System.out.println("----集合中的網絡地址:" + dbList.get(i).getUrl());
                    if (dbList.get(i).getUrl() != null && dbList.get(i).getUrl().equals(play_url)) {
                        try {
                            db.delete(dbList.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        dbList.get(i).setLocalurl(localurl);
                        dbList.get(i).setCurState(3);//下载完成
                        System.out.println("----接收到信息地址:" + localurl);

                        try {
                            db.save(dbList.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mHandler.sendEmptyMessage(101);
            } else if ("PAUSE".equals(action)) {
//                String speed = speeds.get(play_url);
//                intent.getIntExtra("index", -1);
//                speeds.put(play_url, "PAUSE " + speed);
//                if (adapter != null) {
//                    adapter.notifyDataSetInvalidated();
//                    adapter.notifyDataSetChanged();
//                } else {
//                    adapter = new HuancunAdapter();
//                    lv.setAdapter(adapter);
//                }

            } else if ("STARTLocalCache".equals(action)) {
                String localurl = intent.getStringExtra("localurl");
                System.out.println("----接收到STARTLocalCache:" + localurl);
            }
        }
    }

    public boolean delete(String fileName) {

        //SDPATH目录路径，fileName文件名

        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory()) {
            return false;
        }
        file.delete();

        return true;
    }
    public void hideCache(boolean flag){
        if (flag){
            cache_no1.setVisibility(View.VISIBLE);
            cache_no1.setVisibility(View.VISIBLE);
        }else{
            cache_no1.setVisibility(View.GONE);
            cache_no1.setVisibility(View.GONE);
        }
    }

    /**
     * 记录上次触摸的横坐标值
     */
    private float mLastMotionX;

    private VelocityTracker mVelocityTracker;
    public Image3DSwitchView.OnMovechangeListener changeLisener;

    int downX, downY;
    int upX, upY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);

        System.out.println("---touch事件触发");
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
            mCurrentImg = img3D.getImgIndex() - 1;
            if (mCurrentImg < 0) {
                mCurrentImg = dbList.size() - 1;
            }
            clickVedio(mCurrentImg);
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

    private static final int SNAP_VELOCITY = 600;
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
