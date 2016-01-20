package com.hotcast.vr.pageview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.imageView.Image3DSwitchView;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by liurongzhi on 2016/1/13.
 */
public class LocalListView extends BaseView {
    @InjectView(R.id.id_sv)
    Image3DSwitchView id_sv;
    LinearLayout ll_downloading;
    TextView tv_pecent, tv_title;
    TextView tv_speed;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    String play_url;
    String title;
    String media_id;
    private Details details;
    Button bt_delete;

    @OnClick(R.id.ivBack)
    void clickBack() {
        activity.finish();
    }

    public LocalListView(BaseActivity activity) {
        super(activity, R.layout.view_vr_gallery_cache);
        initView();
    }

    /**
     * @param flag true表示显示，false表示隐藏
     */
    public void hideOrShowCache_no(boolean flag) {
        if (flag) {
            tv_title.setVisibility(View.VISIBLE);
            bt_delete.setVisibility(View.VISIBLE);
            System.out.println("---显示"+flag);
        } else {
            System.out.println("---显示"+flag);
            tv_title.setVisibility(View.GONE);
            bt_delete.setVisibility(View.GONE);
        }
        hideOrShowLoading(flag);
    }

    public void hideOrShowLoading(boolean flag) {
        if (flag) {
            ll_downloading.setVisibility(View.VISIBLE);
        } else {
            ll_downloading.setVisibility(View.GONE);
        }
    }

    public void RefreshTextView(String text) {
        if (!text.contains("FINISH")) {
            String[] texts = text.split(" ");
            tv_pecent.setText(texts[0]);
            tv_speed.setText(texts[1]);
        }
    }

    public void initView() {
        ll_downloading = (LinearLayout) getRootView().findViewById(R.id.ll_downloading);
        tv_pecent = (TextView) getRootView().findViewById(R.id.tv_pecent);
        tv_speed = (TextView) getRootView().findViewById(R.id.tv_speed);
        tv_title = (TextView) getRootView().findViewById(R.id.tv_title);
        bt_delete = (Button) getRootView().findViewById(R.id.bt_delete);
    }
}
