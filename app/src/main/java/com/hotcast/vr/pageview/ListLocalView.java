package com.hotcast.vr.pageview;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.PlayerVRActivity;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.ViewUtils;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/6/15.
 */
public class ListLocalView extends BaseView {

    @InjectView(R.id.lv)
    ListView lv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    void clickfab(){
        activity.clickVrMode();
    }

    private QuickAdapter adapter;

    public ListLocalView(BaseActivity activity) {
        super(activity, R.layout.view_local_list);
    }

    private void initListView(){

        List<ListBean> list = MediaDownloadManager.getAll(activity);

        adapter = new QuickAdapter<ListBean>(activity, R.layout.item_list, list) {
            @Override
            protected void convert(BaseAdapterHelper helper, ListBean item) {
                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv);
//                View download = ButterKnife.findById(helper.getView(), R.id.download);
//                download.setVisibility(View.GONE);

                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
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

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListBean item = (ListBean) adapter.getItem(i);
//                if(!item.isDownloadSuccessed()){
//                    activity.showToast("下载未完成");
//                    return ;
//                }
                Intent intent = new Intent(activity, PlayerVRActivity.class);
                intent.putExtra("url", item.getLocalPath());
                intent.putExtra("splite_screen", false);
                activity.startActivity(intent);
            }
        });

        // init fab
        fab.attachToListView(lv);

        startRefreshList();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<ListBean> list = MediaDownloadManager.getAll(activity);
            adapter.notifyData(list);
            startRefreshList();
        }
    };

    private void startRefreshList(){
        if(MediaDownloadManager.isDownloading(activity)){
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,1000);
        }
    }

    @Override
    public void init() {
        initListView();
        super.init();
    }
}
