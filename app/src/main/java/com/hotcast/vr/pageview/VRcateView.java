package com.hotcast.vr.pageview;

import android.content.Intent;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.R;
import com.hotcast.vr.VRcateActivity;
import com.hotcast.vr.VrListActivity;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/7/15.
 */
public class VRcateView extends BaseView {
    @InjectView(R.id.grid_vr_cate)
    GridView gridcate;


    @OnClick(R.id.rl_phone)
    void clickPhone() {
        activity.finish();
    }

    BitmapUtils bu;
    private String requestUrl;
    private BaseAdapter adapter;
    //未选中图标
    private ArrayList<String> imgs = new ArrayList<>();

    //选中图标
    private ArrayList<String> imgs_selected = new ArrayList<>();
    private ArrayList<VrPlay> vrPlayArrayList = new ArrayList<>();

    public VRcateView(BaseActivity activity) {
        super(activity, R.layout.view_vr_cate);
        requestUrl = Constants.URL_CLASSIFY_TITLTE;
        init();
    }

    private void initListView() {
        bu = new BitmapUtils(activity);

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return size;
            }

            @Override
            public Object getItem(int position) {
                return imgs.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(activity, R.layout.item_cate, null);
                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_item_cate);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
//                holder.iv.setImageState();
                bu.display(holder.iv, imgs.get(position));
                return convertView;
            }
        };
        imgs.clear();
        imgs.addAll(BaseApplication.strs);
        gridcate.setAdapter(adapter);

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
        activity.httpPost(mUlr, params, new RequestCallBack<String>() {
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

                Intent intent = new Intent(activity, VrListActivity.class);
                intent.putExtra("channel_id", channel_id);
                intent.putExtra("vrPlays", (Serializable) vrPlays);
                System.out.println("跳转到VrListActivity vrPlays" + vrPlays);
                activity.startActivity(intent);
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

    class ViewHolder {
        ImageView iv;
    }

    private List<Classify> classifies;
    private int size;

    @Override
    public void init() {

        if (checkRequest()) {
            DbUtils db = DbUtils.create(activity);
            try {
                classifies = db.findAll(Classify.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (classifies != null) {
                size = classifies.size();
                for (int i = 0; i < size; i++) {
                    Classify classify = classifies.get(i);
//                    imgs.add(classify.getImage());
                    imgs_selected.add(classify.getImage_click());
                    BaseApplication.strs.add(classify.getImage());
                    System.out.println("---VRcateView classify.getImage():" + classify.getImage());
                    System.out.println("---VRcateView classify.getImage_click():" + classify.getImage_click());
                }
                System.out.println("--VRCateView() size = " + size);
                initListView();
            }else {

            }
        }
        super.init();
    }

//    public void shuaxin(int position) {
//        imgs.clear();
//        imgs.addAll(BaseApplication.strs);
//        String s =imgs_selected.get(position);
//        imgs.remove(position);
//        imgs.add(position,s);
//        adapter.notifyDataSetChanged();
//    }

    public GridView getGridcate() {
        return gridcate;
    }
}
