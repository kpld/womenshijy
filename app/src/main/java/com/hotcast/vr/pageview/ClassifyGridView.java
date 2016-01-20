package com.hotcast.vr.pageview;


import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
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
public class ClassifyGridView extends BaseView {
    @InjectView(R.id.grid)
    PullToRefreshGridView grid;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.progressBar2)
    ProgressBar progressBar2;

    private QuickAdapter adapter;
    private String requestUrl;
    private boolean bPullDown = true;
    private  String channel_id;
    private RequestParams params;
//    private int n = 1;
    private  int page = 1;
    private Intent intent;
    @OnClick(R.id.fab)
    void clickfab(){
        activity.clickVrMode();
    }
    BitmapUtils bitmapUtils;
    public ClassifyGridView(BaseActivity activity,String channel_id){
        super(activity, R.layout.layout_classify_grid);
        this.channel_id = channel_id;
        requestUrl = Constants.URL_CLASSIFY_LIST;
    }
    private void initListView(){
        grid.setMode(PullToRefreshBase.Mode.BOTH);
        grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = true;
                getNetData(page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = false;
                System.out.println("***ClassfyGridView() page" + page++);
                getNetData(page);
            }
        });


        adapter = new QuickAdapter<HomeRoll>(activity, R.layout.item_grid) {
            @Override
            protected void convert(BaseAdapterHelper helper, HomeRoll item) {
                bitmapUtils = new BitmapUtils(activity);
                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv_grid);
//                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
                bitmapUtils.display(iv,item.getImage());
//                helper.setImageUrl(R.id.iv_grid, item.getImage());
                helper.setText(R.id.tv_grid, item.getTitle());
                helper.setText(R.id.desc, item.getDesc());
            }
        };

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeRoll item = (HomeRoll) adapter.getItem(i);
                switch (item.getAction()){
                    case "web":
                        break;
                    case "one":
                        intent = new Intent(activity,DetailActivity.class);
                        intent.putExtra("action","one");
                        intent.putExtra("resource",item.getResource());
                        L.e("ClassifyGridView " + item.getResource());
                        activity.startActivity(intent);
                        break;
                    case "many":
                        intent = new Intent(activity,DetailActivity.class);
                        intent.putExtra("action","many");
                        intent.putExtra("resource",item.getResource());
                        L.e("ClassifyGridView " + item.getResource());
                        activity.startActivity(intent);
                        break;
                }
            }
        });

        // init fab
        fab.attachToListView(grid.getRefreshableView());
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
            grid.onRefreshComplete();
            return;
        }
        params = new RequestParams();
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("page", "1");
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
                grid.onRefreshComplete();
                L.e("responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                grid.onRefreshComplete();

            }
        });
    }

    List<HomeRoll> tmpList;
    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        }
        progressBar2.setVisibility(View.GONE);
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
