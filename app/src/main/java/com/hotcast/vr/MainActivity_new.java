package com.hotcast.vr;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.pageview.BaseView;
import com.hotcast.vr.pageview.ClassifyView;
import com.hotcast.vr.pageview.HomeView2;

import com.hotcast.vr.pageview.MineView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;


/**
 * Created by lostnote on 15/11/17.
 */
public class MainActivity_new extends BaseActivity {
    @InjectView(R.id.layout_content)
    MyViewPager content;

    @InjectView(R.id.main_radio)
    RadioGroup radioGroup;

    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.rl_agreement)
    ScrollView rl_agreement;
    @InjectView(R.id.cb_agreement)
    CheckBox cb_agreement;
    @InjectView(R.id.iv_noNet)
    ImageView nonet;

    private int curTabIndex = -1;

    private MyPagerAdapter adapter;


    private String message = "检测到本程序有新版本发布，建议您更新！";

    private BaseView view0, view1, view2;
    private BaseView[] views = new BaseView[3];
    private List<View> vs;
    private List<String> titles;
    private int[] checkedId = {R.id.page_home, R.id.page_classify, R.id.page_mine};
    private UpdateAppManager updateAppManager;
    String newFeatures;

    @Override
    public int getLayoutId() {
        return R.layout.layout_main;
    }

    @Override
    public void init() {
        if (isNetworkConnected(this) || isWifiConnected(this) || isMobileConnected(this)) {
            nonet.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(spec)) {
                updateAppManager = new UpdateAppManager(this, spec, force, newFeatures);
                updateAppManager.checkUpdateInfo();
            }
            if (isFrist1) {
                System.out.println("***显示免责声明同时提示用户操作帮助");
                rl_agreement.setVisibility(View.VISIBLE);

            }
            view0 = new HomeView2(this);
            view1 = new ClassifyView(this);
            view2 = new MineView(this);
            views[0] = view0;
            views[1] = view1;
            views[2] = view2;

            vs = new ArrayList<View>();
            vs.add(view0.getRootView());
            vs.add(view1.getRootView());
            vs.add(view2.getRootView());
            adapter = new MyPagerAdapter(vs);
            titles = new ArrayList<String>();
            titles.add("热播");
            titles.add("分类");
            titles.add("我的");
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    switch (checkedId) {
                        case R.id.page_home:
                            title.setText(titles.get(0));
                            content.setCurrentItem(0);
                            break;
                        case R.id.page_classify:
                            title.setText(titles.get(1));
                            content.setCurrentItem(1);
                            break;
                        case R.id.page_mine:
                            title.setText(titles.get(2));
                            content.setCurrentItem(2);
                            break;
                    }
                }
            });
            radioGroup.check(R.id.page_home);
            content.setAdapter(adapter);

            content.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    BaseView baseView = views[position];
                    baseView.getRootView();

                }

                @Override
                public void onPageSelected(int position) {
                    title.setText(titles.get(position));
                    radioGroup.check(checkedId[position]);
                    clickTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            clickTab(0);
            cb_agreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    rl_agreement.setVisibility(View.GONE);
                }
            });
        } else {
            nonet.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            nonet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getLayoutId();
//                    getIntent();
                    init();
                }
            });
        }
    }

    private void clickTab(int index) {
        if (index != curTabIndex) {
            changeViewIndex(index);
            curTabIndex = index;
        }
    }


    private void changeViewIndex(int index) {
        views[index].init();
    }

    //下载路径
    private String spec;
    //是否强制更新
    private int force;
    private boolean isFrist1;

    @Override
    public void getIntentData(Intent intent) {
        spec = getIntent().getStringExtra("spec");
        force = getIntent().getIntExtra("force", 0);
        newFeatures = getIntent().getStringExtra("newFeatures");
        isFrist1 = getIntent().getBooleanExtra("isFrist1", isFrist1);
        System.out.println("***isFrist = " + isFrist1);
        sp.add("spec", spec);
        sp.add("force", force);
        System.out.println("---spec = " + spec + "force = " + force);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean bExitting;

    @Override
    public void onBackPressed() {
        if (!bExitting) {
            bExitting = true;
            showToast("再按一次退出");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    bExitting = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
