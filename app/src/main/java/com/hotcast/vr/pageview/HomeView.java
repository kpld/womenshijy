package com.hotcast.vr.pageview;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.HomeBean;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by lostnote on 15/11/17.
 */
public class HomeView extends BaseView {
    @InjectView(R.id.lv_home)
    PullToRefreshListView pulltofresh;
    @InjectView(R.id.fab_home)
    FloatingActionButton fab;


    @OnClick(R.id.fab_home)
    void clickfab(){
        activity.clickVrMode();
    }

    //    需要传递给ViewPager去显示的图片关联文字说明
    private List<String> titleList = new ArrayList<String>();

    //    传递图片对应的url地址的集合
    private List<String> urlImgList = new ArrayList<String>();
    //    放置点得集合
    private List<View> viewList = new ArrayList<View>();
    //    放置底部item条目数据的集合
//    private List<Object> gridList = new ArrayList<Object>();

    private View home_roll;
    private LinearLayout roll_ViewPager;
    private QuickAdapter adapter;
    private String requestUrl;
    private boolean bPullDown = true;
    private String urlHead;
    public HomeView(BaseActivity activity) {
        super(activity, R.layout.layout_home);
//        requestUrl = Constants.URL_MEDIA_PANO;
        init();
    }


    private void initListView() {

        adapter = new QuickAdapter<ListBean>(activity, R.layout.layout_home_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final ListBean item) {
                ItemView iv = ButterKnife.findById(helper.getView(),R.id.itemview);

                com.hotcast.vr.tools.ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);

//                helper.setImageUrl(R.id.iv, item.getImg().getUrls());
//                helper.setText(R.id.tv, item.getName());
               for (int i=0; i < item.getTotal(); i++){
                   urlImgList.add(item.getImg().getUrl());
                   titleList.add(item.getName());

               }
//                urlHead = "192.168.1.101/RedBabyServer/images/";
//                List<HomeMovie> list = new ArrayList<HomeMovie>();
                for (int i = 0; i < 5; i++) {
//                    HomeMovie  m1 = new HomeMovie();
//                    m1.setImgUrl(urlHead+i+".png");
//                    System.out.println(urlHead+i+".png");
//                    m1.setMovieText();
//                    list.add(m1);
                }
//                System.out.println(list.get(1).getImgUrl());
//                System.out.println(list.get(3).getImgUrl());
//                iv =(ItemView) findViewById(R.id.itemview);
//                iv.setItemMovies(getActivity(), item);

            }
        };

    }

    @Override
    public void init() {

        if (bFirstInit) {
            initListView();
        }

        if (checkRequest()) {
            getNetData();
        }
        super.init();
    }

    private void getNetData() {
        if (Utils.textIsNull(requestUrl)) {
            pulltofresh.onRefreshComplete();
            return;
        }
        String url = requestUrl ;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token","123");
        activity.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                bDataProcessed = true;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
//                hideRefreshView();
                L.e("HomeView2 responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                Toast.makeText(activity, "网络连接异常", Toast.LENGTH_SHORT).show();
//                hideRefreshView();

            }
        });
    }


    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        }


//        List<ListBean> tmpList = new Gson().fromJson(json, new TypeToken<List<ListBean>>() {
//        }.getType());
//        if (bPullDown) {
//            adapter.addNewAll(tmpList);
//        } else {
//            adapter.addAll(tmpList);
//        }
//        L.e("adapter size=" + adapter.getCount());
        HomeBean homeBean = new Gson().fromJson(json,HomeBean.class);
        L.e("HomeView2 homeRolls = " + homeBean);
    }


    private ListBean itemForDownload;

//    private void getNetData(String id) {
//        String url = String.format(Constants.URL_MEDIA_DETAIL, id);
//        L.e("get media detail url=" + url);
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
//                    if (null != itemForDownload) {
//                        itemForDownload.setUrls(mUrl);
//                        ((MainActivity) activity).downloadMedia(itemForDownload);
//                        itemForDownload = null;
//                        return;
//                    }
//
//                        Intent intent = new Intent(activity, PlayerVRActivity.class);
//                        intent.putExtra("url", mUrl);
//                        intent.putExtra("splite_screen", false);
//                        activity.startActivity(intent);
//
//                } catch (Exception e) {
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
