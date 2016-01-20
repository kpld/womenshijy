package com.hotcast.vr;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.pageview.VRcateView;
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

import java.io.Serializable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * Created by joey on 8/7/15.
 */
public class VRcateActivity extends BaseActivity {

    private VRcateView view1;
    private VRcateView view2;
    GridView gridView1;
    GridView gridView2;
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;

    private int curCateIndex = -1;

    private static final int CATE_SIZE = 4;



    private void changeIndex(int index) {
        if (curCateIndex != index) {
//            view1.changeIndex(index);
//            view2.changeIndex(index);
            curCateIndex = index;
        }
    }

    @Override
    public int getLayoutId() {

        return R.layout.activity_vr_cate;
    }

    private List<Classify> classifies;

    @Override
    public void init() {
        DbUtils db = DbUtils.create(this);
        try {
            classifies = db.findAll(Classify.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        inintView();



    }
    private void inintView(){
        view1 = new VRcateView(this);
        view2 = new VRcateView(this);
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gridView1 = (GridView) view1.getRootView().findViewById(R.id.grid_vr_cate);
        gridView2 = view2.getGridcate();
        gridView1.setSelection(0);
        gridView2.setSelection(0);
//        view2.shuaxin(0);
//        view1.shuaxin(0);
        gridView1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                view2.shuaxin(position);
//                view1.shuaxin(position);
                gridView2.setSelection(position);
                System.out.println("---VRcateActivity**gridView1 现在选择的是第" + position + "个item");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridView2.setSelection(position);
                System.out.println("---VRcateActivity**gridView1 现在点击的是第" + position + "个item");
                getNetData(classifies.get(position).getChannel_id());
            }
        });

        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gridView1.setSelection(position);
                System.out.println("---VRcateActivity**gridView1 现在点击的是第" + position + "个item");
                getNetData(classifies.get(position).getChannel_id());
            }

        });
        gridView2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                view2.shuaxin(position);
//                view1.shuaxin(position);
                gridView1.setSelection(position);
                System.out.println("---VRcateActivity**gridView2 现在选择的是第" + position + "个item");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        changeIndex(0);
        gridView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView2.setSelection(position);
                System.out.println("---VRcateActivity**gridView1 现在你长按着的是第" + position + "个item");
                return false;
            }
        });
        gridView2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView1.setSelection(position);
                System.out.println("---VRcateActivity**gridView2 现在你长按着的是第" + position + "个item");
                return false;
            }
        });
    }

    @Override
    public void getIntentData(Intent intent) {

    }

    List<VrPlay> vrPlays;

    public void getNetData(final String channel_id) {
        String mUlr = Constants.URL_VR_PLAY;
        System.out.println("***VrListActivity *** getNetData()" + mUlr);
        L.e("播放路径 mUrl=" + mUlr);
        RequestParams params = new RequestParams();
        System.out.println("***VrListActivity *** getNetData()" + params);
        params.addBodyParameter("token", "123");
        params.addBodyParameter("channel_id", channel_id);
        System.out.println("***VrListActivity *** getNetData()" + channel_id);
        this.httpPost(mUlr, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                System.out.println("***VrListActivity *** onStart()");
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("***VrListActivity *** onSuccess()" + responseInfo.result);
                if (Utils.textIsNull(responseInfo.result)) {
                    return;
                }
                vrPlays = new Gson().fromJson(responseInfo.result, new TypeToken<List<VrPlay>>() {
                }.getType());
                System.out.println("***VrListActivity *** onSuccess()" + vrPlays);
                System.out.println("***VrListActivity *** onSuccess()" + vrPlays.size());

                Intent intent = new Intent(VRcateActivity.this, VrListActivity.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("vrPlays", (Serializable) vrPlays);
                System.out.println("跳转到VrListActivity vrPlays" + vrPlays);
                VRcateActivity.this.startActivity(intent);
                BaseApplication.size = vrPlays.size();
//                for (int i = 0; i < vrPlays.size(); i++){
//                    vrPlayArrayList.add(vrPlays.get(i));
//                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("---VrListActivity *** onTouchEvent");
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                System.out.println("---VrListActivity *** ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("---VrListActivity *** ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("---VrListActivity *** ACTION_UP");
                break;
        }
        return true;
    }
}
