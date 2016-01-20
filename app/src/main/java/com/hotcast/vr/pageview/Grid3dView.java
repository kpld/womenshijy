package com.hotcast.vr.pageview;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/6/15.
 */
public class Grid3dView extends BaseView {

    @InjectView(R.id.grid)
    PullToRefreshGridView grid;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    private QuickAdapter adapter;
    private String requestUrl;
    private int type;
    private boolean bPullDown = true;

    @OnClick(R.id.fab)
    void clickfab(){
        activity.clickVrMode();
    }

    public Grid3dView(BaseActivity activity, int type) {
        super(activity, R.layout.view_3d_grid);
        this.type = type;
//        switch (type) {
//            case 0:
//                requestUrl = Constants.URL_MEDIA_PANO;
//                break;
//            case 1:
//                requestUrl = Constants.URL_MEDIA_3D;
//                break;
//            case 2:
//                requestUrl = Constants.URL_IMAX+"?limit=9";
//                break;
//            case 4:
//                break;
//        }
    }

    private void initListView(){
        grid.setMode(PullToRefreshBase.Mode.BOTH);
        grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = true;
                getNetData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = false;
                getNetData();
            }
        });


        adapter = new QuickAdapter<ListBean>(activity, R.layout.item_grid) {
            @Override
            protected void convert(BaseAdapterHelper helper, ListBean item) {
//                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv);
//                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
                helper.setImageUrl(R.id.iv, item.getImg().getUrl());
                helper.setText(R.id.tv, item.getName());
            }
        };

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListBean item = (ListBean) adapter.getItem(i);
//                getNetData(item.getId());
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
            getNetData();
        }
        super.init();
    }

    private void getNetData() {
        if (Utils.textIsNull(requestUrl)) {
            grid.onRefreshComplete();
            return;
        }
        String url = requestUrl;
        if(!bPullDown){
            url+="&offset="+adapter.getCount();
        }
        activity.httpGet(url, new RequestCallBack<String>() {
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


    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        }

        List<ListBean> tmpList = new Gson().fromJson(json, new TypeToken<List<ListBean>>() {
        }.getType());
        if(bPullDown){
            adapter.addNewAll(tmpList);
        }else{
            adapter.addAll(tmpList);
        }

        L.e("adapter size="+adapter.getCount());
    }


//    private void getNetData(String id) {
//        String url = String.format(Constants.URL_MEDIA_DETAIL, id);
//        L.e("get media detail url="+url);
//        activity.httpGet(url, new RequestCallBack<String>() {
//            @Override
//            public void onStart() {
//                super.onStart();
//                activity.showLoading("准备影片中...");
//            }
//
//            @Override
//            public void onSuccess(ResponseInfo<String> responseInfo) {
//                activity.hideLoading();
//                L.e("result="+responseInfo.result);
//                MediaDetailBean bean = GsonUtil.Json2Bean(responseInfo.result, MediaDetailBean.class);
//                try {
//                    String mUrl = bean.getEpisodes().get(0).getUrls().get(0).getUrls();
//
//                    Class clz = null;
//                    switch (type) {
//                        case 0:
//                            clz = PlayerVRActivity.class;
//                            break;
//                        case 1:
//                            break;
//                        case 2:
//                            clz = PlayerSBSActivity.class;
//                            break;
//                        case 3:
//                            break;
//                    }
//                    if (null != clz) {
//                        Intent intent = new Intent(activity, clz);
//                        intent.putExtra("url", mUrl);
//                        intent.putExtra("splite_screen", false);
//                        activity.startActivity(intent);
//                    }
//                } catch (Exception e) {
////                    finish();
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                activity.hideLoading();
//            }
//        });
//    }
}
