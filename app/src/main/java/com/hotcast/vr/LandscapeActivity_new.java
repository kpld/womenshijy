package com.hotcast.vr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.adapter.GalleyAdapter;
import com.hotcast.vr.bean.ChanelData;
import com.hotcast.vr.bean.Channel;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.pageview.LandGalleyView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.HotVedioCacheUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.SaveBitmapUtils;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class LandscapeActivity_new extends BaseActivity {
//    @InjectView(R.id.container1)
//    RelativeLayout container1;
    //    @InjectView(R.id.container2)
//    RelativeLayout container2;
    View loadingBar1;
    View loadingBar2;
    View nointernet1;
    View nointernet2;

    boolean isloading = false;
    LandGalleyView gallery1;
    LandGalleyView gallery2;
    GalleyAdapter adapter1;
    GalleyAdapter adapter2;
    private List<ImageView> mImages1;
    private List<ImageView> mImages2;
    private BitmapUtils bitmapUtils;
    private int nowPosition;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_landscape;
    }

    @Override
    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        bitmapUtils = new BitmapUtils(this);
        mImages1 = new ArrayList<>();
        mImages2 = new ArrayList<>();
        loadingBar1 = findViewById(R.id.loadingbar1);
        loadingBar2 = findViewById(R.id.loadingbar2);
        nointernet1 = findViewById(R.id.nointernet1);
        nointernet2 = findViewById(R.id.nointernet2);
        showOrHideLoadingBar(false);
        showNoInternetDialog(false);

        gallery1 = (LandGalleyView) findViewById(R.id.gallery1);
        gallery2 = (LandGalleyView) findViewById(R.id.gallery2);

        for (int i = 0; i < netClassifys.size(); i++) {
            ImageView iv1 = new ImageView(this);
            ImageView iv2 = new ImageView(this);
            bitmapUtils.display(iv1, netClassifys.get(i).getImg_big());
            bitmapUtils.display(iv2, netClassifys.get(i).getImg_big());
            iv1.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 120), DensityUtils.dp2px(this, 120)));
            iv1.setBackgroundResource(R.drawable.buttom_selector);
            iv1.setPadding(DensityUtils.dp2px(this, 0), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 0), 0);
            iv2.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 120), DensityUtils.dp2px(this, 120)));
            iv2.setBackgroundResource(R.drawable.buttom_selector);
            iv2.setPadding(DensityUtils.dp2px(this, 0), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 0), 0);
            mImages1.add(iv1);
            mImages2.add(iv2);
        }
        ImageView iv1 = new ImageView(this);
        ImageView iv2 = new ImageView(this);
        iv1.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 120), DensityUtils.dp2px(this, 120)));
        iv1.setBackgroundResource(R.drawable.buttom_selector);
        iv1.setPadding(DensityUtils.dp2px(this, 0), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 0), 0);
        iv2.setLayoutParams(new Gallery.LayoutParams(DensityUtils.dp2px(this, 120), DensityUtils.dp2px(this, 120)));
        iv2.setBackgroundResource(R.drawable.buttom_selector);
        iv2.setPadding(DensityUtils.dp2px(this, 0), DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 0), 0);
        iv1.setImageResource(R.mipmap.cache_icon);
        iv2.setImageResource(R.mipmap.cache_icon);
        mImages1.add(iv1);
        mImages2.add(iv2);
        adapter1 = new GalleyAdapter(mImages1);
        adapter2 = new GalleyAdapter(mImages2);
        gallery1.setAdapter(adapter1);
        gallery2.setAdapter(adapter2);
        if (mImages1.size()>2){
            gallery1.setSelection(1);
            gallery2.setSelection(1);
        }
        gallery1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("---position" + position);
                nowPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    List<ChanelData> netClassifys;
    //下载路径
    private String spec;
    //是否强制更新
    private String is_force;
    //更新日志
    String newFeatures;

    @Override
    public void getIntentData(Intent intent) {
        spec = getIntent().getStringExtra("spec");

        is_force = getIntent().getStringExtra("is_force");
        newFeatures = getIntent().getStringExtra("newFeatures");
        Channel channel = (Channel) getIntent().getSerializableExtra("channel");
        netClassifys = (List<ChanelData>) getIntent().getSerializableExtra("netClassifys");
        if (channel != null) {
            netClassifys = channel.getData();
        }
        System.out.println("---netClassifys2"+netClassifys);
    }

    @Override
    protected void onDestroy() {
        BaseApplication.is3Dbitmap = false;
        super.onDestroy();
    }


    int downX;
    int moveX;
    int upX;
    int downY;
    int moveY;
    int upY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        System.out.println("---length:" + length);
        if (length < 20 && !isloading) {
            //执行点击事件
            clickItem(nowPosition);
            return true;
        }
        gallery1.onTouchEvent(event);
        gallery2.onTouchEvent(event);
        return true;
    }
    public void clickItem(int i) {
//        System.out.println("---clickItem"+i);
        if (i < netClassifys.size()) {
            showOrHideLoadingBar(true);
            getNetData(netClassifys.get(i).getId());
        } else if (i == netClassifys.size()) {
            //查询本地指定的缓存文件夹
//            if (BaseApplication.doAsynctask) {
                DbUtils db = DbUtils.create(LandscapeActivity_new.this);
                try {
                    dbList = db.findAll(LocalBean.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (dbList == null) {
                    dbList = new ArrayList<>();
                }
                Intent cacheIntent = new Intent(LandscapeActivity_new.this, LocalCachelActivity_new.class);
                cacheIntent.putExtra("dbList", (Serializable) dbList);
                System.out.println("---传递数据的尺寸：" + dbList.size());
                startActivity(cacheIntent);
//            } else {
////                显示小菊花
//                showOrHideLoadingBar(true);
//                showOrHideLoadingBar(true);
//                Message msg = Message.obtain();
//                msg.what = 1;
//                mhandler.sendMessageDelayed(msg, 1000);
//            }
        }

    }
    List<ChannelList> tmpList;
    public void getNetData(final String channel_id) {
        String mUlr = Constants.PROGRAM_LIST;
        System.out.println("***VrListActivity *** getNetData()" + mUlr);
        L.e("播放路径 mUrl=" + mUlr);
        RequestParams params = new RequestParams();
        System.out.println("***VrListActivity *** getNetData()" + params);
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("page", "1");
        params.addBodyParameter("page_size", String.valueOf(500));
        System.out.println("***VrListActivity *** getNetData()" + channel_id);
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                System.out.println("***VrListActivity *** onStart()");
                super.onStart();
            }
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showOrHideLoadingBar(false);
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                tmpList = new Gson().fromJson(responseInfo.result, new TypeToken<List<ChannelList>>() {
                }.getType());
                Intent intent = new Intent(LandscapeActivity_new.this, LandscapeActivity_Second.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("tmpList", (Serializable) tmpList);
                LandscapeActivity_new.this.startActivity(intent);
                BaseApplication.size = tmpList.size();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showOrHideLoadingBar(false);
                showNoInternetDialog(true);
            }
        });
    }
    public void showOrHideLoadingBar(boolean flag){
        if (flag){
            isloading =flag;
            loadingBar1.setVisibility(View.VISIBLE);
            loadingBar2.setVisibility(View.VISIBLE);
        }else{
            isloading =flag;
            loadingBar1.setVisibility(View.GONE);
            loadingBar2.setVisibility(View.GONE);
        }
    }
    public void showNoInternetDialog(boolean flag) {
        if (flag){
            nointernet1.setVisibility(View.VISIBLE);
            nointernet2.setVisibility(View.VISIBLE);
            mhandler.sendEmptyMessageDelayed(0, 2000);
        }else{
            nointernet1.setVisibility(View.GONE);
            nointernet2.setVisibility(View.GONE);
        }
    }

    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showNoInternetDialog(false);
                    break;
                case 1:
                    if (BaseApplication.doAsynctask){
                        DbUtils db = DbUtils.create(LandscapeActivity_new.this);
                        try {
                            dbList = db.findAll(LocalBean.class);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (dbList == null) {
                            dbList = new ArrayList<>();
                        }
                        showOrHideLoadingBar(false);
                        showOrHideLoadingBar(false);
                        Intent cacheIntent = new Intent(LandscapeActivity_new.this, LocalCachelActivity.class);
                        cacheIntent.putExtra("dbList", (Serializable) dbList);
                        System.out.println("---传递数据的尺寸：" + dbList.size());
                        startActivity(cacheIntent);
                    }else{
                        mhandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //本地缓存的集合，在异步中处理
    List<LocalBean> dbList = null;
    boolean dataCacheOk = false;

    class MyAsyncTask extends AsyncTask<Integer, Integer, List<LocalBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List doInBackground(Integer... params) {
            DbUtils db = DbUtils.create(LandscapeActivity_new.this);
            try {
                dbList = db.findAll(LocalBean.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (dbList == null) {
                dbList = new ArrayList<>();
            }
            if (dbList != null) {
                System.out.println("---数据库原始尺寸：" + dbList.size());
            }
            String[] localNames = HotVedioCacheUtils.getVedioCache(BaseApplication.VedioCacheUrl);
            if (localNames != null) {
                int size = dbList.size();
                List<String> titles = new ArrayList<>();
                for (LocalBean localBean : dbList){
                    titles.add(localBean.getTitle());
                }
                for (int i = 0; i < localNames.length; i++) {
                    String title = localNames[i];
                    String title1 = title.replace(".mp4","");

                    if (size == 0) {
                        System.out.println("---title1"+title1);
                        LocalBean localBean = new LocalBean();
                        localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setCurState(3);
                        SaveBitmapUtils.saveMyBitmap(title1, VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
//                        System.out.println("---数据库没有数据。添加本地bitmap：" + VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                        localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                        localBean.setUrl("");
                        localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setTitle(title1);
                        dbList.add(localBean);
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!titles.contains(title1)){
                            System.out.println("---不为空title1"+title1);
                            LocalBean localBean = new LocalBean();
                            localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                            localBean.setCurState(3);
                            SaveBitmapUtils.saveMyBitmap(title.replace(".mp4", ""), VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                            localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                            localBean.setUrl("");
                            localBean.setTitle(title1);
                            localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                            dbList.add(localBean);
                            try {
                                db.saveOrUpdate(localBean);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            System.out.println("--"+title);
                        }
                    }
                }
            }
            System.out.println("----数据处理完毕");
            dataCacheOk = true;
            return dbList;
        }

        @Override
        protected void onPostExecute(List<LocalBean> s) {
//            super.onPostExecute(s);
            System.out.println("----数据处理完毕");
            dataCacheOk = true;

        }
    }
}
