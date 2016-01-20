package com.hotcast.vr;

import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 15/12/3.
 */
public class HelpActivity extends BaseActivity {
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;

    @OnClick(R.id.iv_return)
    void clickReturn(View v){
        finish();
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_help;
    }

    @Override
    public void init() {
        title.setText("帮助");
        iv_return.setVisibility(View.VISIBLE);

    }

    @Override
    public void getIntentData(Intent intent) {

    }
}
