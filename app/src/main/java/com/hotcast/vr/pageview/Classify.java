package com.hotcast.vr.pageview;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.pagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/17.
 */
public class Classify extends BaseView {
    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.indicator)
    TabPageIndicator indicator;

    private MyPagerAdapter adapter;
    private int curTabIndex = -1;



    private BaseView view0, view1, view2, view3, view4;
    private BaseView[] views = new BaseView[5];
    private List<View> vs;
    private List<String> titles;

    public Classify(BaseActivity activity){
        super(activity, R.layout.layout_classify);
        System.out.println("构造函数" + pager);
        init();
    }

    @Override
    public void init() {
        if(bFirstInit){
            System.out.println("***init()");
            initListView();
        }

        super.init();
    }

    private void initListView() {
        System.out.println("init()"+pager);

        titles = new ArrayList<String>();
        titles.add("占星公寓");
        titles.add("体育");
        titles.add("动漫");
        titles.add("旅游");
        titles.add("现场");

        vs = new ArrayList<View>();

        if (adapter == null){
            adapter = new MyPagerAdapter(vs,titles);
            pager.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        //指针需要和ViewPager进出绑定，即指针指向那页，ViewPager页处在那页
        indicator.setViewPager(pager);
        System.out.println("指针绑定ViewPager");
        indicator.setCurrentItem(0);


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

}
