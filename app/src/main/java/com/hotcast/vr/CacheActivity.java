package com.hotcast.vr;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/18.
 */
public class CacheActivity extends BaseActivity{
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_return)
    ImageView back;
    @InjectView(R.id.gv_cache)
    GridView gv_cache;
    private QuickAdapter adapter;
    @InjectView(R.id.fl_cache)
    FrameLayout fl_cache;

    private List<ListBean> list;
    @Override
    public int getLayoutId() {
        return R.layout.layout_cache;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<ListBean> list = MediaDownloadManager.getAll(CacheActivity.this);
            adapter.notifyData(list);
            startRefreshList();
        }
    };
    @Override
    public void init() {
        title.setText("离线缓存");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 添加一个出去的动画
                AlphaAnimation animation = new AlphaAnimation(1.0f,0.1f);
                animation.setDuration(3000);
                
                finish();
            }
        });
    }
    private void initview(){
        adapter = new QuickAdapter<ListBean>(this,R.layout.layout_item_cache,list) {
            @Override
            protected void convert(BaseAdapterHelper helper, ListBean item) {
                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv_cache_item);

                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(CacheActivity.this) / 2);
                helper.setImageUrl(R.id.iv, item.getImg().getUrl());

                String surfix = "";
                if(item.getCurState() == ListBean.STATE_DOWNLOADING){
                    surfix = "("+(int)(item.getCurrent()*100/item.getTotal()) +"%)";
                }else if(item.getCurState() == ListBean.STATE_SUCCESS){
                    surfix = "(下载完成)";
                }else if(item.getCurState() == ListBean.STATE_FAILED){
                    surfix = "(下载失败)";
                }
                helper.setText(R.id.tv, item.getName()+surfix);
            }
        };
        gv_cache.setAdapter(adapter);
        gv_cache.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListBean item = (ListBean) adapter.getItem(i);
                Intent intent = new Intent(CacheActivity.this, PlayerVRActivityNew.class);
                intent.putExtra("title",item.getName());
                intent.putExtra("url", item.getLocalPath());
                intent.putExtra("splite_screen", false);
                CacheActivity.this.startActivity(intent);
            }
        });
    }
    private void startRefreshList(){
        if(MediaDownloadManager.isDownloading(CacheActivity.this)){
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,1000);
        }
    }
    @Override
    public void getIntentData(Intent intent) {
        list = MediaDownloadManager.getAll(this);
        if (list == null){
            fl_cache.setVisibility(View.VISIBLE);
        }else {
            fl_cache.setVisibility(View.GONE);
            initview();
            startRefreshList();
        }
    }
}
