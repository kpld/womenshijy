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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.ListLocalActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.bean.HomeBean;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.bean.HomeSubject;
import com.hotcast.vr.pullrefreshview.PullToRefreshBase;
import com.hotcast.vr.pullrefreshview.PullToRefreshListView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;


/**
 * Created by lostnote on 15/11/17.
 */
public class HomeView2 extends BaseView {

    @ViewInject(R.id.top_news_title)
    private TextView top_news_title;

    @ViewInject(R.id.dots_ll)
    private LinearLayout dots_ll;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;


    private FloatingActionButton fab_home;
    private ProgressBar progressBar4;
    private MyBaseAdapter myBaseAdapter;
    //    需要传递给ViewPager去显示的图片关联文字说明
    private List<String> titleList = new ArrayList<String>();

    //    传递图片对应的url地址的集合
    private List<String> urlImgList = new ArrayList<String>();
    //    放置点得集合
    private List<View> viewList = new ArrayList<View>();
    //    放置底部item条目数据的集合
    private List<HomeSubject> subjects;

    private View layout_roll_view;
    private LinearLayout ll_top_news_viewpager;
    private com.hotcast.vr.pullrefreshview.PullToRefreshListView ptrlv_lv_item_news;
    //    private ItemView itemView;
    private String requestUrl;
    //    private boolean bPullDown = true;
    List<ItemView> itemViews;

    public HomeView2(BaseActivity activity) {
        super(activity, R.layout.layout_home);//根布局
        requestUrl = Constants.URL_HOME;
        init();
    }


    private void initListView() {
        layout_roll_view = View.inflate(activity, R.layout.layout_roll_view, null);//轮播图
        ViewUtils.inject(this, layout_roll_view);
        ll_top_news_viewpager = (LinearLayout) layout_roll_view.findViewById(R.id.ll_top_news_viewpager);
        rootView = View.inflate(activity, R.layout.frag_item_news, null);
        ViewUtils.inject(this, rootView);
        fab_home = (FloatingActionButton) rootView.findViewById(R.id.fab_home);
        progressBar4 = (ProgressBar) rootView.findViewById(R.id.progressBar4);
        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickVrMode();
            }
        });
        ptrlv_lv_item_news = (PullToRefreshListView) rootView.findViewById(R.id.lv_item_news);
        //下拉加载的事件屏蔽
        ptrlv_lv_item_news.setPullLoadEnabled(false);
        //包含下拉刷新，上拉加载操作
        ptrlv_lv_item_news.setScrollLoadEnabled(true);
        ptrlv_lv_item_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                Toast.makeText(activity, "下拉刷新了数据", Toast.LENGTH_SHORT).show();
                // TODO: 15/11/22  在这里执行相应的访问网络的操作
                urlImgList.clear();
                titleList.clear();
                getNetData();

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                hideRefreshView();
            }
        });

        ptrlv_lv_item_news.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

//                Toast.makeText(activity, "listview条目被点击了", Toast.LENGTH_SHORT).show();

            }
        });
        hideRefreshView();
        getNetData();

    }


    public void hideRefreshView() {
        ptrlv_lv_item_news.onPullDownRefreshComplete();
        ptrlv_lv_item_news.onPullUpRefreshComplete();
    }

    @Override
    public void init() {
//        initListView();
        if (bFirstInit) {
            //第一次打开，则初始化view，后续只要刷新数据就行
            initListView();
        }

        if (checkRequest()) {
            getNetData();
        }
        super.init();
    }

    private void getNetData() {
        if (Utils.textIsNull(requestUrl)) {
            hideRefreshView();
            return;
        }
        String url = requestUrl;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
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
                hideRefreshView();
                L.e("HomeView2 responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                iv_noNet.setVisibility(View.VISIBLE);
                activity.showToast("网络连接异常");
                hideRefreshView();

            }
        });

    }

    List<HomeRoll> homeRolls;
    HomeBean homeBean;

    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            iv_noNet.setVisibility(View.VISIBLE);
//            return;
        } else {
            try {
                homeBean = new Gson().fromJson(json, HomeBean.class);
                homeRolls = homeBean.getHome_roll();

            } catch (IllegalStateException e) {
                activity.showToast("解析出现错误，请刷新数据");
            }
            //初始化viewpager
            L.e("HomeView2 viewList.sixe() = " + viewList.size());
            RollViewPager rollViewPager = new RollViewPager(activity, viewList, new RollViewPager.onPageClick() {
                @Override
                public void onclick(int i) {
                    Intent intent = new Intent(activity, DetailActivity.class);
                    intent.putExtra("action", homeRolls.get(i).getAction());
                    intent.putExtra("resource", homeRolls.get(i).getResource());
                    activity.startActivity(intent);
//                Toast.makeText(activity, "position = " + i, Toast.LENGTH_SHORT).show();
                }
            });

//        HomeSubject的集合
//        if (subjects == null){
//            subjects = new ArrayList<>();
//        }else{
//            subjects.clear();
//        }
            System.out.println("HomeView2 subjects = " + subjects);
            subjects = homeBean.getHome_subject();
//        subjects.addAll(homeBean.getHome_subject());
            urlImgList.clear();
            titleList.clear();
            for (int i = 0; i < homeRolls.size(); i++) {
                HomeRoll homeRoll = homeRolls.get(i);
                urlImgList.add(homeRoll.getImage());
                titleList.add(homeRoll.getTitle());
            }
            initDot();
            rollViewPager.initTitleList(top_news_title, titleList);
            rollViewPager.initImgUrlList(urlImgList);
            rollViewPager.startRoll();
            ll_top_news_viewpager.removeAllViews();
            ll_top_news_viewpager.addView(rollViewPager);
            //就是个listView
            if (ptrlv_lv_item_news.getRefreshableView().getHeaderViewsCount() < 1) {
                ptrlv_lv_item_news.getRefreshableView().addHeaderView(layout_roll_view);
            }

            //填充listView
            if (itemViews == null) {
                itemViews = new ArrayList<>();
            } else {
                itemViews.clear();
            }
            for (int i = 0; i < subjects.size(); i++) {
                ItemView itemView = new ItemView(activity);
                itemView.setItemList(activity, subjects.get(i));
                itemViews.add(itemView);
            }
            if (myBaseAdapter == null) {
                if (subjects != null) {
                    myBaseAdapter = new MyBaseAdapter();
                }
                ptrlv_lv_item_news.getRefreshableView().setAdapter(myBaseAdapter);
            } else {
                myBaseAdapter.notifyDataSetChanged();
            }
        }
        progressBar4.setVisibility(View.GONE);
    }

    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return subjects.size();
        }

        @Override
        public Object getItem(int i) {
            return subjects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = new ItemView(activity);
//            }
//            ((ItemView) convertView).setItemList(activity, subjects.get(position));
            convertView = itemViews.get(position);
            convertView.invalidate();
            return convertView;
        }
    }
    private void initDot() {
        dots_ll.removeAllViews();
        viewList.clear();

        for (int i = 0; i < urlImgList.size(); i++) {
            View view = new View(activity);
            if (i == 0) {
                view.setBackgroundResource(R.drawable.dot_focus);
            } else {
                view.setBackgroundResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
            view.setLayoutParams(layoutParams);
            layoutParams.setMargins(5, 0, 10, 0);
            dots_ll.addView(view);
            viewList.add(view);
        }
    }

}
