package com.hotcast.vr.pageview;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.pagerindicator.TabPageIndicator;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/17.
 */
public class ClassifyView extends BaseView {
    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.indicator)
    TabPageIndicator indicator;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;

    private MyPagerAdapter adapter;
    private int curTabIndex = -1;


    private List<Classify> classifies;
    private int size;
    private BaseView[] views;
    private List<View> vs;
    private List<String> titles;

    private String requestUrl;

    public ClassifyView(BaseActivity activity){
        super(activity, R.layout.layout_classify);
        requestUrl = Constants.URL_CLASSIFY_TITLTE;
        init();
    }
    @Override
    public void init() {
        if (checkRequest()) {
            getNetData();
        }
        super.init();
    }

    private void initListView() {
        DbUtils db = DbUtils.create(activity);

        for (int i = 0 ; i < size; i++){
            Classify classify = classifies.get(i);
            try {
                db.delete(Classify.class, WhereBuilder.b("channel_id", "==", classifies.get(i).getChannel_id()));
                db.save(classify);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        views = new BaseView[size];
        L.e("ClassifyView viewa.lenght = " + size);
        titles = new ArrayList<>();

        vs = new ArrayList<>();
        for (int i = 0;i < size; i++){
            titles.add(classifies.get(i).getTitle());
            switch (classifies.get(i).getShow_type()){
                case "big":
                    views[i] = new ClassifyListView(activity,classifies.get(i).getChannel_id());
                    break;
                case "small":
                    views[i] = new ClassifyGridView(activity,classifies.get(i).getChannel_id());
                    break;
            }
            vs.add(views[i].getRootView());
        }
        if (vs != null && vs.size() > 0) {
            if (adapter == null) {
                adapter = new MyPagerAdapter(vs, titles);
                pager.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            //指针需要和ViewPager进出绑定，即指针指向那页，ViewPager页处在那页
            indicator.setViewPager(pager);
            L.e("指针绑定ViewPager");
            System.out.println("指针绑定ViewPager");
            indicator.setCurrentItem(0);
        }else {
            iv_noNet.setVisibility(View.VISIBLE);
        }
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
//                classify_radio.check(checkedId[position]);
                indicator.setCurrentItem(position);
                BaseView baseView = views[position];
                baseView.init();
                baseView.getRootView();
                clickTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        BaseView baseView = views[0];
        baseView.init();
        clickTab(0);

    }
    private void clickTab(int index){
        if(index != curTabIndex){
            changeViewIndex(index);
            curTabIndex = index;
        }
    }
    private void changeViewIndex(int index){
        views[index].init();
    }

    private void getNetData() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
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
                L.e("ClassifyView  responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
            }
        });
    }


    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            iv_noNet.setVisibility(View.VISIBLE);
        }else {
            classifies = new Gson().fromJson(json, new TypeToken<List<Classify>>() {
            }.getType());
            size = classifies.size();
            L.e("adapter size=" + classifies.size());
            initListView();
        }
    }

}
