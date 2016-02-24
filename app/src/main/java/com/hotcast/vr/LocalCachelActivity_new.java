package com.hotcast.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotcast.vr.adapter.LocalGalleyAdapter;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.pageview.LandGalleyView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

public class LocalCachelActivity_new extends BaseActivity {
    List<LocalBean> dbList;//本地视频集合
    boolean noData;
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;
    LandGalleyView gallery1;
    LandGalleyView gallery2;
    LocalGalleyAdapter adapter1;
    LocalGalleyAdapter adapter2;
    private List<String> mImages1;
    private int nowPosotion = 0;

    private LinearLayout ll_downloading1;
    private TextView tv_pecent1;
    private TextView tv_speed1;
    private TextView tv_title1;
    private LinearLayout ll_downloading2;
    private TextView tv_pecent2;
    private TextView tv_speed2;
    private TextView tv_title2;
    private ViewGroup view1;
    private ViewGroup view2;
    private DetailReceiver receiver;
    List<String> ids;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_new;
    }

    @Override
    public void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ids = new ArrayList<>();
        if (dbList == null || dbList.size() <= 0) {
            noData = false;
            container1.setBackgroundResource(R.mipmap.backgroud_heng_nodata);
            container2.setBackgroundResource(R.mipmap.backgroud_heng_nodata);
        } else {
            noData = true;
            gallery1 = (LandGalleyView) findViewById(R.id.gallery1);
            gallery2 = (LandGalleyView) findViewById(R.id.gallery2);
            mImages1 = new ArrayList<>();
            for (int i = 0; i < dbList.size(); i++) {
                mImages1.add(dbList.get(i).getImage());
            }
            adapter1 = new LocalGalleyAdapter(mImages1, this);
            adapter2 = new LocalGalleyAdapter(mImages1, this);

            gallery1.setAdapter(adapter1);
            gallery2.setAdapter(adapter2);
            initView();
            gallery1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    nowPosotion = position;
                    tv_title1.setText(dbList.get(position).getTitle());
                    tv_title2.setText(dbList.get(position).getTitle());
                    if (dbList.get(position).isDownloading()) {
                        ll_downloading1.setVisibility(View.VISIBLE);
                        ll_downloading2.setVisibility(View.VISIBLE);
                    } else {
                        ll_downloading1.setVisibility(View.INVISIBLE);
                        ll_downloading2.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            for (int i = 0; i < dbList.size(); i++) {
                ids.add(dbList.get(i).getId());
            }
        }
        receiver = new DetailReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
    }

    public void initView() {
        ll_downloading1 = (LinearLayout) findViewById(R.id.ll_downloading1);
        tv_pecent1 = (TextView) findViewById(R.id.tv_pecent1);
        tv_speed1 = (TextView) findViewById(R.id.tv_speed1);
        tv_title1 = (TextView) findViewById(R.id.tv_title1);

        ll_downloading2 = (LinearLayout) findViewById(R.id.ll_downloading2);
        tv_pecent2 = (TextView) findViewById(R.id.tv_pecent2);
        tv_speed2 = (TextView) findViewById(R.id.tv_speed2);
        tv_title2 = (TextView) findViewById(R.id.tv_title2);
        if (!noData) {
            tv_title1.setVisibility(View.INVISIBLE);
            tv_title2.setVisibility(View.INVISIBLE);
        }
        view1 = (ViewGroup) findViewById(R.id.delete_window1);
        view2 = (ViewGroup) findViewById(R.id.delete_window2);
        view1.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void getIntentData(Intent intent) {
        dbList = (List<LocalBean>) getIntent().getSerializableExtra("dbList");
    }

    int downX;
    int moveX;
    int upX;
    int downY;
    int moveY;
    int upY;
    long downTime;
    long moveTime;
    boolean pOd = false;//false 表示播放模式，true表示删除模式
    Boolean isSamePress = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noData) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSamePress = false;
                    downTime = System.currentTimeMillis();
                    downX = (int) event.getX();
                    downY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveTime = System.currentTimeMillis();
                    moveX = (int) event.getX();
                    moveY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    upX = (int) event.getX();
                    upY = (int) event.getY();
                    int xlen = Math.abs(downX - upX);
                    int ylen = Math.abs(downY - upY);
                    int length = (int) Math.sqrt((double) xlen * xlen + (double) ylen * ylen);
                    if (length < 20) {
                        if (!isSamePress) {
                            if (pOd) {
                                view1.setVisibility(View.INVISIBLE);
                                view2.setVisibility(View.INVISIBLE);
                                System.out.println("---单击事件");
                                pOd = false;
                            } else {
                                //执行点击事件
                                clickItem();
                                System.out.println("---点击事件");
                                return true;
                            }
                            return true;
                        }

                    }
            }

            int lengthY = Math.abs(moveY - downY);
            int lengthX = Math.abs(moveX - downX);
            int lengthXY = (int) Math.sqrt((double) lengthX * lengthX + (double) lengthY * lengthY);
//        System.out.println("---length:" + length);
            System.out.println("---lengthXY:" + lengthXY);
            if (moveTime - downTime > 1500 && lengthXY < 200 && !isSamePress) {
                isSamePress = true;
                if (!pOd) {
                    pOd = true;//进入删除模式
                    System.out.println("---显示窗体");
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                } else {
                    pOd = false;//进入播放模式
                    view1.setVisibility(View.INVISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    System.out.println("---删除执行");
                    deleteVedio();
                }
                return true;
            }
            if (lengthX > 50) {
                gallery1.onTouchEvent(event);
                gallery2.onTouchEvent(event);
                return true;
            }

        }
        return false;
    }

    public void clickItem() {
       int state =  dbList.get(nowPosotion).getCurState();
        if (state == 3){
            Intent intent = new Intent(LocalCachelActivity_new.this, PlayerVRActivityNew.class);
            intent.putExtra("play_url", dbList.get(nowPosotion).getLocalurl());
            intent.putExtra("title", dbList.get(nowPosotion).getTitle());
            intent.putExtra("splite_screen", true);
            startActivity(intent);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
//                    setSpeedAndPecent();
                    break;
            }
        }
    };
    String action;

    public void setSpeedAndPecent(String speed, String pecent) {
        ll_downloading2.setVisibility(View.VISIBLE);
        ll_downloading1.setVisibility(View.VISIBLE);
        System.out.println("---需要显示：" + speed + "---" + pecent);
        tv_pecent1.setText("已下载" + pecent + "%");
        tv_speed1.setText(speed + "KB/S");
        tv_pecent2.setText("已下载" + pecent + "%");
        tv_speed2.setText(speed + "KB/S");
    }

    /**
     * 这是接受下载进度的广播接受者
     */
    class DetailReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String speed = "";
            action = intent.getAction();
            if ("DOWNLOADING".equals(action)) {
                long total = intent.getLongExtra("total", -1);//电影总大小
                String vid = intent.getStringExtra("vid");//電影的下載地址，作為電影的唯一标识
                //执行相应操作
                long current = intent.getLongExtra("current", -1);//电影当前进度
                int index = ids.indexOf(vid);
                LocalBean localBean = dbList.get(index);
                localBean.setSpeed((current - localBean.getCurrent()) / 1024 + "");
                localBean.setCurrent(current);
                localBean.setPecent(((current * 100) / total) + "");
                if (nowPosotion == index) {
                    System.out.println("---需要显示：" + localBean.getTitle() + "--" + total);
                    setSpeedAndPecent(localBean.getSpeed(), localBean.getPecent());
                }
                System.out.println("---DOWNLOADING：" + dbList.get(index).getTitle());
//
            } else if ("FINISH".equals(action)) {
//                下載完畢，執行下載完畢的邏輯
                String vid = intent.getStringExtra("vid");
                long total = intent.getLongExtra("total", -1);//电影总大小
                int index = ids.indexOf(vid);
                LocalBean localBean = dbList.get(index);
                localBean.setCurState(3);
                localBean.setPecent("100");
                localBean.setCurrent(total);
                localBean.setSpeed("0");
                localBean.setDownloading(false);
                System.out.println("---FINISH：" + localBean.getTitle() + "--" + dbList.get(index).getTitle() + dbList.get(index).isDownloading());
                if (nowPosotion == index) {
                    ll_downloading2.setVisibility(View.INVISIBLE);
                    ll_downloading1.setVisibility(View.INVISIBLE);
                }
//                mHandler.sendEmptyMessage(101);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                event.startTracking();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                //执行删除操作
                deleteVedio();
                break;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    public void deleteVedio() {
        if (noData) {
            mImages1.remove(nowPosotion);
            //删除数据库和本地中的缓存文件
            LocalBean localBean = dbList.get(nowPosotion);
            DbUtils db = DbUtils.create(this);
            try {
                db.deleteById(LocalBean.class,localBean.getId());
            } catch (DbException e) {
                e.printStackTrace();
            }
            dbList.remove(nowPosotion);
            deleteLocalFile(localBean.getLocalurl());
            if (mImages1.size() > 0) {
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            } else {
                noData = false;
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
                tv_title1.setVisibility(View.INVISIBLE);
                tv_title2.setVisibility(View.INVISIBLE);
                container1.setBackgroundResource(R.mipmap.backgroud_heng_nodata);
                container2.setBackgroundResource(R.mipmap.backgroud_heng_nodata);
            }
        }
    }

    public boolean deleteLocalFile(String fileName) {

            //SDPATH目录路径，fileName文件名

            File file = new File(fileName);
            if (file == null || !file.exists() || file.isDirectory()) {
                return false;
            }
            file.delete();

            return true;
    }
}
