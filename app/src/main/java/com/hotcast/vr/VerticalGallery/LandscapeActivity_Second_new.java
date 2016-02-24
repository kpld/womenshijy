//package com.hotcast.vr.VerticalGallery;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.Gallery;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.hotcast.vr.BaseActivity;
//import com.hotcast.vr.R;
//import com.hotcast.vr.adapter.GalleyAdapter;
//import com.hotcast.vr.bean.ChannelList;
//import com.hotcast.vr.pageview.LandGalleyView;
//import com.hotcast.vr.tools.DensityUtils;
//import com.lidroid.xutils.BitmapUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class LandscapeActivity_Second_new extends BaseActivity {
//    List<ChannelList> tmpList;
//    String channel_id;//当前频道的ID
//    boolean nodata = true;//是否有数据
//    private ArrayList<String> titles = new ArrayList<>();
//    private ArrayList<String> descs = new ArrayList<>();
//    private List<ImageView> mImages1;
//    private List<ImageView> mImages2;
//    LandGalleyView gallery1;
//    LandGalleyView gallery2;
//    GalleyAdapter adapter1;
//    GalleyAdapter adapter2;
//    BitmapUtils bitmapUtils;
//    private int nowPosition;
//    private int nowPage = 1;
//    View loadingBar1;
//    View loadingBar2;
//    View nointernet1;
//    View nointernet2;
//    boolean isloading = false;
//
//    TextView tv_title1;
//    TextView tv_desc1;
//    TextView bt_ceach1;
//    TextView tv_page1;
//    TextView tv_title2;
//    TextView tv_desc2;
//    TextView bt_ceach2;
//    TextView tv_page2;
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_landscape_activity__second;
//    }
//
//    @Override
//    public void init() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        bitmapUtils = new BitmapUtils(this);
//        initView();
//        initData();
//        showOrHideLoadingBar(false);
//        showNoInternetDialog(false);
//
//        gallery1 = (LandGalleyView) findViewById(R.id.gallery1);
//        gallery2 = (LandGalleyView) findViewById(R.id.gallery2);
//        adapter1 = new GalleyAdapter(mImages1);
//        adapter2 = new GalleyAdapter(mImages2);
//        gallery1.setAdapter(adapter1);
//        gallery2.setAdapter(adapter2);
//        if (mImages1.size() > 2) {
//            gallery1.setSelection(1);
//            gallery2.setSelection(1);
//        }
//        gallery1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("---position" + position);
//                nowPosition = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }
//
//    /**
//     * 初始化数据
//     */
//    public void initData() {
//        mImages1 = new ArrayList<>();
//        mImages2 = new ArrayList<>();
//        for (int i = 0; i < tmpList.size(); i++) {
//            ImageView iv1 = new ImageView(this);
//            ImageView iv2 = new ImageView(this);
//            bitmapUtils.display(iv1, tmpList.get(i).getImage().get(0));
//            bitmapUtils.display(iv2, tmpList.get(i).getImage().get(0));
//            iv1.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 118), DensityUtils.dp2px(this, 80)));
//            iv1.setPadding(DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5));
//            iv1.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv1.setBackgroundResource(R.drawable.buttom_selector_second);
//            iv2.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 118), DensityUtils.dp2px(this, 80)));
//            iv2.setPadding(DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5));
//            iv2.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv2.setBackgroundResource(R.drawable.buttom_selector_second);
//            mImages1.add(iv1);
//            mImages2.add(iv2);
//        }
//    }
//
//    public void initView() {
//        loadingBar1 = findViewById(R.id.loadingbar1);
//        loadingBar2 = findViewById(R.id.loadingbar2);
//        nointernet1 = findViewById(R.id.nointernet1);
//        nointernet2 = findViewById(R.id.nointernet2);
//        tv_title1 = (TextView) findViewById(R.id.tv_title1);
//        tv_desc1 = (TextView) findViewById(R.id.tv_desc1);
//        bt_ceach1 = (TextView) findViewById(R.id.bt_ceach1);
//        tv_page1 = (TextView) findViewById(R.id.tv_page1);
//
//        tv_title2 = (TextView) findViewById(R.id.tv_title2);
//        tv_desc2 = (TextView) findViewById(R.id.tv_desc2);
//        bt_ceach2 = (TextView) findViewById(R.id.bt_ceach2);
//        tv_page2 = (TextView) findViewById(R.id.tv_page2);
//    }
//
//    /**
//     * 改变影片的信息及状态
//     */
//    public void changeVideoInfo() {
//
//    }
//
//    @Override
//    public void getIntentData(Intent intent) {
//        tmpList = (List<ChannelList>) getIntent().getSerializableExtra("tmpList");
//        System.out.println("---数据的尺寸：" + tmpList.size());
//        channel_id = getIntent().getStringExtra("channel_id");
//        if (tmpList.size() == 0) {
//            nodata = false;
//        } else {
//            for (int i = 0; i < tmpList.size(); i++) {
//                ChannelList vrPlay = tmpList.get(i);
//                titles.add(vrPlay.getTitle());
//                descs.add(vrPlay.getDesc());
//            }
//        }
//    }
//
//    int downX;
//    int moveX;
//    int upX;
//    int downY;
//    int moveY;
//    int upY;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downX = (int) event.getX();
//                downY = (int) event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                upX = (int) event.getX();
//                upY = (int) event.getY();
//                break;
//        }
//        int xlen = Math.abs(downX - upX);
//        int ylen = Math.abs(downY - upY);
//        int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
//        int lengthY = Math.abs(upY-downY);
//        if (lengthY>100){
//
//        }
//        System.out.println("---length:" + length);
//        if (length < 20 && !isloading) {
//            //执行点击事件
//            clickItem(nowPosition);
//            return true;
//        }
//        gallery1.onTouchEvent(event);
//        gallery2.onTouchEvent(event);
//        return true;
//    }
//
//    public void clickItem(int i) {
////        if (i < netClassifys.size()) {
////            showOrHideLoadingBar(true);
////            getNetData(netClassifys.get(i).getId());
////        } else if (i == netClassifys.size()) {
////            System.out.println("---点击本地缓存");
////            //查询本地指定的缓存文件夹
////            if (BaseApplication.doAsynctask) {
////                DbUtils db = DbUtils.create(LandscapeActivity_new.this);
////                try {
////                    dbList = db.findAll(LocalBean.class);
////                } catch (DbException e) {
////                    e.printStackTrace();
////                }
////                if (dbList == null) {
////                    dbList = new ArrayList<>();
////                }
////                Intent cacheIntent = new Intent(LandscapeActivity_new.this, LocalCachelActivity.class);
////                cacheIntent.putExtra("dbList", (Serializable) dbList);
////                System.out.println("---传递数据的尺寸：" + dbList.size());
////                startActivity(cacheIntent);
////            } else {
//////                显示小菊花
////                showOrHideLoadingBar(true);
////                showOrHideLoadingBar(true);
////                Message msg = Message.obtain();
////                msg.what = 1;
////                mhandler.sendMessageDelayed(msg, 1000);
////            }
////        }
//
//    }
//
//    public void showOrHideLoadingBar(boolean flag) {
//        if (flag) {
//            isloading = flag;
//            loadingBar1.setVisibility(View.VISIBLE);
//            loadingBar2.setVisibility(View.VISIBLE);
//        } else {
//            isloading = flag;
//            loadingBar1.setVisibility(View.GONE);
//            loadingBar2.setVisibility(View.GONE);
//        }
//    }
//
//    public void showNoInternetDialog(boolean flag) {
//        if (flag) {
//            nointernet1.setVisibility(View.VISIBLE);
//            nointernet2.setVisibility(View.VISIBLE);
//            mhandler.sendEmptyMessageDelayed(0, 2000);
//        } else {
//            nointernet1.setVisibility(View.GONE);
//            nointernet2.setVisibility(View.GONE);
//        }
//    }
//
//    Handler mhandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    showNoInternetDialog(false);
//                    break;
//                case 1:
////
//                    break;
//            }
//        }
//    };
//}
