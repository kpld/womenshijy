package com.hotcast.vr.pageview;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.R;
import com.hotcast.vr.VrListActivity;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.VrPlay;
import com.hotcast.vr.image3D.Image3DView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by lostnote on 16/1/8.
 */
public class LandscapeView extends BaseView implements View.OnClickListener{
    @InjectView(R.id.img_1)
    Image3DView img_1;
    @InjectView(R.id.img_2)
    Image3DView img_2;
    @InjectView(R.id.img_3)
    Image3DView img_3;
    @InjectView(R.id.img_4)
    Image3DView img_4;
    @InjectView(R.id.img_5)
    Image3DView img_5;
    @InjectView(R.id.img_6)
    Image3DView img_6;

    private List<Classify> classifies;
    public LandscapeView(BaseActivity activity) {
        super(activity, R.layout.layout_landscape);
        getNetDate();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_1:
                getNetData(classifies.get(0).getChannel_id());
                break;
            case R.id.img_2:
                getNetData(classifies.get(1).getChannel_id());
                break;
            case R.id.img_3:
                getNetData(classifies.get(2).getChannel_id());
                break;
            case R.id.img_4:
                getNetData(classifies.get(3).getChannel_id());
                break;
            case R.id.img_5:
                getNetData(classifies.get(4).getChannel_id());
                break;
            case R.id.img_6:
                getNetData(classifies.get(5).getChannel_id());
                break;

        }
    }

    private void getNetDate(){
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
        activity.httpPost(Constants.URL_CLASSIFY_TITLTE, params, new RequestCallBack<String>() {
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
                if (Utils.textIsNull(responseInfo.result)){
                    activity.showToast("网络异常，请检查网络");
                    return;
                }else {
                    classifies = new Gson().fromJson(responseInfo.result, new TypeToken<List<Classify>>() {
                    }.getType());
                    img_1.setOnClickListener(LandscapeView.this);
                    img_2.setOnClickListener(LandscapeView.this);
                    img_3.setOnClickListener(LandscapeView.this);
                    img_4.setOnClickListener(LandscapeView.this);
                    img_5.setOnClickListener(LandscapeView.this);
                    img_6.setOnClickListener(LandscapeView.this);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                activity.showToast("网络异常，请检查网络");
                bDataProcessed = false;
                bProcessing = false;
            }
        });
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

}
