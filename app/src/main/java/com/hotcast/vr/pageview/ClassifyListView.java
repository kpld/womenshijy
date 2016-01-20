package com.hotcast.vr.pageview;


import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.tools.ViewUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 15/11/17.
 */
public class ClassifyListView extends BaseView {

    @InjectView(R.id.list)
    PullToRefreshListView list;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.progressBar3)
    ProgressBar progressBar3;

    @OnClick(R.id.fab)
    void clickfab(){
        activity.clickVrMode();
    }

    private QuickAdapter adapter;
    private String requestUrl;
    private boolean bPullDown = true;
    private  String channel_id;
    private  int page = 1;
    private RequestParams params;
    private Intent intent;
    BitmapUtils bitmapUtils;
    public ClassifyListView(BaseActivity activity,String channel_id) {
        super(activity, R.layout.layout_classify_list);
        this.channel_id = channel_id;
        requestUrl = Constants.URL_CLASSIFY_LIST;
    }

    private void initListView(){
        bitmapUtils = new BitmapUtils(activity);
        list.setMode(PullToRefreshBase.Mode.BOTH);
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                bPullDown = true;
                getNetData(page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                bPullDown = false;
                getNetData(++page);

            }
        });

        adapter = new QuickAdapter<HomeRoll>(activity, R.layout.item_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, final HomeRoll item) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                LinearLayout iv = ButterKnife.findById(helper.getView(), R.id.iv_list);
                bitmapUtils.display(iv, item.getImage());
//                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
//                helper.setImageUrl(R.id.iv_list, item.getImage());
                helper.setText(R.id.tv, item.getTitle());
                helper.setText(R.id.show_time, item.getShow_times()+"已看");
                helper.setText(R.id.desc, item.getDesc());
            }
        };

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeRoll item = (HomeRoll) adapter.getItem(i - 1);
                switch (item.getAction()){
                    case "web":
                        break;
                    case "one":
                        intent = new Intent(activity,DetailActivity.class);
                        intent.putExtra("action","one");
                        intent.putExtra("resource",item.getResource());
                        L.e("ClassifyListView " + item.getResource());
                        activity.startActivity(intent);
                        break;
                    case "many":
                        intent = new Intent(activity,DetailActivity.class);
                        intent.putExtra("action","many");
                        intent.putExtra("resource",item.getResource());
                        L.e("ClassifyListView " + item.getResource());
                        activity.startActivity(intent);
                        break;
                }

            }
        });

        fab.attachToListView(list.getRefreshableView());
    }

    @Override
    public void init() {
        if(bFirstInit){
            initListView();
        }

        if (checkRequest()) {
            getNetData(page);
        }
        super.init();
    }

    private void getNetData(int page) {
        if (Utils.textIsNull(requestUrl)) {
            list.onRefreshComplete();
            return;
        }
        params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        if(!bPullDown){
            params.addBodyParameter("page", String.valueOf(page));
        }
        activity.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                bDataProcessed = true;
                bProcessing = false;
                list.onRefreshComplete();
                L.e("responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                list.onRefreshComplete();

            }
        });
    }
    List<HomeRoll> tmpList;

    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        }
        progressBar3.setVisibility(View.GONE);
        try {
            tmpList = new Gson().fromJson(json, new TypeToken<List<HomeRoll>>() {
            }.getType());
        }catch (IllegalStateException e){
            activity.showToast("解析出现错误，请刷新数据");
        }

        DbUtils db = DbUtils.create(activity);
        try {
            db.delete(HomeRoll.class, WhereBuilder.b("channel_id", "==", channel_id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < tmpList.size(); i ++){
            HomeRoll homeRoll = tmpList.get(i);
            homeRoll.setChannel_id(channel_id);
            try {
                db.save(homeRoll);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        if(bPullDown){
            adapter.addNewAll(tmpList);
        }else{

            adapter.addAll(tmpList);
        }

        L.e("adapter size=" + adapter.getCount());
    }
}
