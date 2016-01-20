package com.hotcast.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.receiver.DownloadReceiver;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by lostnote on 15/11/23.
 */
public class ListLocalActivity extends BaseActivity {
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    //    @InjectView(R.id.head)
//    RelativeLayout head;
    @InjectView(R.id.lv)
    GridView lv;
    @InjectView(R.id.bt_editor)
    Button bt_editor;
    //    @InjectView(R.id.fab)
//    FloatingActionButton fab;
    @InjectView(R.id.cache_no)
    ImageView cache_no;
    @InjectView(R.id.tv_downloded)
    TextView tv_downloded;
    @InjectView(R.id.tv_downloding)
    TextView tv_downloding;

    private boolean editor = false;
    int edit = 1;

    @OnClick(R.id.bt_editor)
    void clickEditor() {
        if (edit % 2 == 0){
            editor = false;
        }else {
            editor = true;
        }
        edit ++;
//        adapter.notify();
        lv.setAdapter(new HuancunAdapter());
        adapter.notifyDataSetChanged();
        System.out.println("你点击了编辑 editor = " + editor);

    }

    @OnClick(R.id.iv_return)
    void onReturn() {
        finish();
    }

    BitmapUtils bu;
    private HuancunAdapter adapter;

    //    private MyAdapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.view_local_list;
    }

    DbUtils db;
    List<LocalBean> list = null;

    DetailReceiver receiver;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

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
        bu = new BitmapUtils(ListLocalActivity.this);
        title.setText("离线缓存");
        iv_return.setVisibility(View.VISIBLE);
        initListView();
    }

    static class ViewHolder {
        ImageView iv_huancun_img;//預覽圖
        ImageView iv_huancun_sd;//暫停或下載中
        TextView tv_huancun_downpecent;//下載進度
        TextView tv_huancun_downspeed;//下載速度
        TextView tv_huancun_moviename;//影片名稱
        ImageView ib_delete;
    }

    private void initListView() {
        db = DbUtils.create(ListLocalActivity.this);
        try {
            list = db.findAll(LocalBean.class);
            if (list == null) {
                tv_downloded.setVisibility(View.GONE);
                tv_downloding.setVisibility(View.GONE);
                bt_editor.setVisibility(View.GONE);
                cache_no.setVisibility(View.VISIBLE);
            } else {
                tv_downloded.setVisibility(View.VISIBLE);
                tv_downloding.setVisibility(View.VISIBLE);
                cache_no.setVisibility(View.GONE);
                bt_editor.setVisibility(View.VISIBLE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list == null || list.size() <= 0) {
            bt_editor.setVisibility(View.GONE);
            cache_no.setVisibility(View.VISIBLE);
        } else {
            cache_no.setVisibility(View.GONE);
            bt_editor.setVisibility(View.VISIBLE);
            if (adapter != null) {
                lv.setAdapter(adapter);
            } else {
                adapter = new HuancunAdapter();
                lv.setAdapter(adapter);
            }


        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String localurl = list.get(i).getLocalurl();
                File file;
                if (localurl != null) {
                    file = new File(localurl);
                } else {
                    file = new File(" ");
                }
                if (!editor) {
                    if (localurl != null && file.exists()) {
//                        System.out.println("---本地地址：" + localurl + "---url:" + list.get(i).getUrl());
                        Intent intent = new Intent(ListLocalActivity.this, PlayerVRActivityNew.class);
                        intent.putExtra("play_url", localurl);
                        intent.putExtra("splite_screen", false);
                        ListLocalActivity.this.startActivity(intent);
                    } else if (localurl == null) {
                        if (list.get(i).getCurState() == -1) {
                            System.out.print("---重新下载");
                            index = -1;
                            Intent intent = new Intent(START);
                            ListLocalActivity.this.sendBroadcast(intent);
                            try {
                                db.delete(list.get(i));
                                list.get(i).setCurState(0);
                                db.save(list.get(i));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        } else if (list.get(i).getCurState() == 0) {
                            System.out.print("---暂停");
                            try {
                                db.delete(list.get(i));
                                list.get(i).setCurState(-1);
                                db.save(list.get(i));
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(PAUSE);
                            intent.putExtra("index", i);
                            index = i;
                            sendBroadcast(intent);
                            if (adapter != null) {
                                adapter.notifyDataSetInvalidated();
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter = new HuancunAdapter();
                                lv.setAdapter(adapter);
                            }

                        }
                    }
                }else {
                    try {
                        if (list.get(i).getLocalurl() != null){
                            delete(list.get(i).getLocalurl());
                        }
                        db.delete(list.get(i));
                       list = db.findAll(LocalBean.class);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (list != null){
                        adapter = new HuancunAdapter();
                        lv.setAdapter(adapter);
                    }else {
                        bt_editor.setVisibility(View.GONE);
                        cache_no.setVisibility(View.VISIBLE);
                        startRefreshList();
                    }
                    System.out.println("****显示差号****删除一个item");
                }
            }
        });


//        fab.attachToListView(lv);

        startRefreshList();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<LocalBean> list = null;
            try {
                list = db.findAll(LocalBean.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            startRefreshList();
        }
    };

    private void startRefreshList() {
        if (MediaDownloadManager.isDownloading(this)) {
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public void getIntentData(Intent intent) {

    }

    class HuancunAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LocalBean bean = list.get(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ListLocalActivity.this, R.layout.huancun_grid_item, null);
                holder.iv_huancun_img = (ImageView) convertView.findViewById(R.id.iv_huancun_img);
                holder.iv_huancun_sd = (ImageView) convertView.findViewById(R.id.iv_huancun_sd);
                holder.ib_delete = (ImageView) convertView.findViewById(R.id.ib_delete);
                holder.tv_huancun_downpecent = (TextView) convertView.findViewById(R.id.tv_huancun_downpecent);
                holder.tv_huancun_downspeed = (TextView) convertView.findViewById(R.id.tv_huancun_downspeed);
                holder.tv_huancun_moviename = (TextView) convertView.findViewById(R.id.tv_huancun_moviename);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            bu.display(holder.iv_huancun_img, list.get(position).getImage());
            holder.tv_huancun_moviename.setText(list.get(position).getTitle());
            String speed = speeds.get(list.get(position).getUrl());

            holder.iv_huancun_sd.setVisibility(View.VISIBLE);
            holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_img);
            holder.tv_huancun_downspeed.setVisibility(View.VISIBLE);
            holder.tv_huancun_downpecent.setVisibility(View.VISIBLE);
            System.out.println("---adapter：" + speed);
            if (speed != null) {
                if ("FINISH".equals(speed)) {
                    holder.iv_huancun_sd.setVisibility(View.GONE);
                    holder.tv_huancun_downspeed.setVisibility(View.GONE);
                    holder.tv_huancun_downpecent.setVisibility(View.GONE);
                } else if (speed.contains("PAUSE")) {
                    String[] strs = speed.split(" ");
                    holder.iv_huancun_sd.setVisibility(View.VISIBLE);
                    holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
                    holder.tv_huancun_downspeed.setText("0KB/S");
                    holder.tv_huancun_downpecent.setText(strs[2]);
                } else {
                    String[] strs = speed.split(" ");
                    holder.tv_huancun_downspeed.setText(strs[0]);
                    holder.tv_huancun_downpecent.setText(strs[1]);
                }
            } else {
                holder.iv_huancun_sd.setVisibility(View.GONE);
                holder.tv_huancun_downspeed.setVisibility(View.GONE);
                holder.tv_huancun_downpecent.setVisibility(View.GONE);
            }
//            if (index != -1) {
                if (index != -1 && position == index){
                    holder.iv_huancun_sd.setVisibility(View.VISIBLE);
                    holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
                    holder.tv_huancun_downspeed.setText("0KB/S");
                }
//            }

            if (editor) {
                holder.ib_delete.setVisibility(View.VISIBLE);

            }else {
                holder.ib_delete.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    Map<String, String> speeds;//下载速度的集合
    long pecent = 0;
    String action;
    String speed;
    int index = -1;

    /**
     * 这是接受下载进度的广播接受者
     */
    class DetailReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            String play_url = intent.getStringExtra("play_url");//電影的下載地址，作為電影的唯一标识
            if ("DOWNLOADING".equals(action)) {
                index = -1;
                //执行相应操作
                long total = intent.getLongExtra("total", -1);//电影总大小
                long current = intent.getLongExtra("current", -1);//电影当前进度
                if (pecent == 0) {
                    pecent = current;
                } else {
                    speed = (current - pecent) / 1024 + "KB/S" + " 已下载" + (current * 100) / total + "%";
                    pecent = current;
                    speeds.put(play_url, speed);
                }
                System.out.println("---接收到的信息：" + speed);
                if (adapter != null) {
                    adapter.notifyDataSetInvalidated();
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new HuancunAdapter();
                    lv.setAdapter(adapter);
                }
            } else if ("FINISH".equals(action)) {
                index = -1;
//                下載完畢，執行下載完畢的邏輯
                speeds.put(play_url, "FINISH");
                String localurl = intent.getStringExtra("localurl");
                System.out.println("----接受到的網絡地址:" + play_url);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("----集合中的網絡地址:" + list.get(i).getUrl());
                    if (list.get(i).getUrl().equals(play_url)) {
                        try {
                            db.delete(list.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        list.get(i).setLocalurl(localurl);
                        list.get(i).setCurState(2);//下载完成
                        System.out.println("----接收到信息地址:" + localurl);

                        try {
                            db.save(list.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println("---接收到的信息：" + "FINISH");
                if (adapter != null) {
                    adapter.notifyDataSetInvalidated();
                } else {
                    adapter = new HuancunAdapter();
                    lv.setAdapter(adapter);
                }
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

            }
        }
    }
    public boolean delete(String fileName) {

        //SDPATH目录路径，fileName文件名

        File file = new File(fileName);
        if (file == null || !file.exists() || file.isDirectory()){
            return false;
        }
        file.delete();

        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
