package com.hotcast.vr.pageview;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.Utils;
import com.hotcast.vr.tools.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/6/15.
 */
public class List3dView extends BaseView {

    @InjectView(R.id.lv)
    PullToRefreshListView lv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    void clickfab(){
        activity.clickVrMode();
    }
    private int type;
    private QuickAdapter adapter;
    private String requestUrl;
    private boolean bPullDown = true;

    public List3dView(BaseActivity activity, int type) {
        super(activity, R.layout.view_3d_list);
        this.type = type;
//        switch (type) {
//            case 0:
//                requestUrl = Constants.URL_MEDIA_PANO;
//                break;
//            case 1:
//                requestUrl = Constants.URL_MEDIA_3D;
//                break;
//            case 2:
//                break;
//            case 4:
//                break;
//        }
    }

    private void initListView(){
        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                bPullDown = true;
                getNetData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                bPullDown = false;
                getNetData();
            }
        });


        adapter = new QuickAdapter<ListBean>(activity, R.layout.item_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ListBean item) {
                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv);
//                View download = ButterKnife.findById(helper.getView(), R.id.download);

                if(type == 0){
//                    download.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            activity.showDialog(null, "是否下载影片?", null, null, new BaseActivity.OnAlertSureClickListener() {
//                                @Override
//                                public void onclick() {
//                                    itemForDownload = item;
////                                    getNetData(itemForDownload.getId());
//                                }
//                            });
////                            ((MainActivity)activity).downloadMedia(item.getId(), item.get);
//
//                        }
//                    });
                }else{
//                    download.setVisibility(View.GONE);
                }

                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
                helper.setImageUrl(R.id.iv, item.getImg().getUrl());
                helper.setText(R.id.tv, item.getName());
            }
        };

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListBean item = (ListBean) adapter.getItem(i - 1);
//                getNetData(item.getId());
            }
        });

        // init fab
        fab.attachToListView(lv.getRefreshableView());
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
            lv.onRefreshComplete();
            return;
        }
        String url = requestUrl;
        if(!bPullDown){
            url+="?offset="+adapter.getCount();
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
                lv.onRefreshComplete();
                L.e("responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                lv.onRefreshComplete();

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

        L.e("adapter size=" + adapter.getCount());
    }


    private ListBean itemForDownload;

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
//                L.e("result=" + responseInfo.result);
//                MediaDetailBean bean = GsonUtil.Json2Bean(responseInfo.result, MediaDetailBean.class);
//                try {
//                    String mUrl = bean.getEpisodes().get(0).getUrls().get(0).getUrls();
//
//                    if(null != itemForDownload){
//                        itemForDownload.setUrls(mUrl);
//                        ((MainActivity)activity).downloadMedia(itemForDownload);
//                        itemForDownload = null;
//                        return ;
//                    }
//
//
//                    Class clz = null;
//                    switch (type) {
//                        case 0:
//                            clz = PlayerVRActivity.class;
//                            break;
//                        case 1:
//                            break;
//                        case 2:
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
//
//            }
//
//            @Override
//            public void onFailure(HttpException e, String s) {
//                activity.hideLoading();
//            }
//        });
//    }
}
