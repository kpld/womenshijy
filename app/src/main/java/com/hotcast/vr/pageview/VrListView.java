package com.hotcast.vr.pageview;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.R;
import com.hotcast.vr.VrListActivity;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.imageView.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.ScreenUtils;
import com.hotcast.vr.tools.ViewUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/6/15.
 */
public class VrListView extends BaseView implements GestureDetector.OnGestureListener {

    @InjectView(R.id.id_sv)
    Image3DSwitchView id_sv;
    @InjectView(R.id.bt_ceach)
    TextView bt_ceach;
    View loadingBar;

    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    String play_url;
    String title;
    String media_id;
    private Details details;

    @OnClick(R.id.ivBack)
    void clickBack() {
        activity.finish();
    }

//    @OnClick(R.id.bt_ceach)
//    void bt_ceach() {
//        if (!BaseApplication.isDownLoad) {
//            activity.showDialog(null, "是否下载影片?", null, null, new BaseActivity.OnAlertSureClickListener() {
//                @Override
//                public void onclick() {
//                    Intent intent = new Intent(START);
////                        intent.putExtra("Details",details);
////                        intent.putExtra("play_url",play_url);
//                    BaseApplication.detailsList.add(details);
//                    BaseApplication.playUrls.add(play_url);
//                    activity.sendBroadcast(intent);
//                    activity.showToast("已经加入下载列表");
////                    tv_cache.setText("已缓存");
//                    BaseApplication.isDownLoad = true;
//                    bt_ceach.setFocusable(false);
//                    DbUtils db = DbUtils.create(activity);
//                    LocalBean localBean = new LocalBean();
//                    localBean.setTitle(title);
//                    localBean.setImage(details.getImage());
//                    localBean.setId(media_id);
//                    localBean.setUrl(play_url);
//                    localBean.setCurState(0);//還沒下載，準備下載
////                        localBean.setLocalurl(localUrl);
//                    try {
//                        db.delete(localBean);
//                        db.save(localBean);
//                    } catch (DbException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//        } else {
//            activity.showToast("亲，您已经缓存了");
//        }
//    }

    private QuickAdapter adapter;

    public boolean isbLocal() {
        return bLocal;
    }

    public void setbLocal(boolean bLocal) {
        this.bLocal = bLocal;
    }

    private boolean bLocal;

    private
    GestureDetector detector = new

            GestureDetector(this);

    public VrListView(BaseActivity activity) {
        super(activity, R.layout.view_vr_gallery);
        initListView();
    }

    public void addData(List<ListBean> list) {
        adapter.addAll(list);
    }

    public int getCount() {
        return adapter.getCount();
    }

    public ListBean getItem(int pos) {
        return (ListBean) adapter.getItem(pos);
    }

    /**
     * 显示或者关闭小菊花
     *
     * @param flag
     */
    public void showOrHideProgressBar(boolean flag) {
        if (flag) {
            loadingBar.setVisibility(View.VISIBLE);
        } else {
            loadingBar.setVisibility(View.GONE);
        }
    }

    private void initListView() {
        loadingBar = this.getRootView().findViewById(R.id.loadingbar);
        loadingBar.setVisibility(View.GONE);
//        id_sv.setOnImageSwitchListener(new Image3DSwitchView.OnImageSwitchListener() {
//            @Override
//            public void onImageSwitch(int currentImage) {
//
//            }
//        });

//        adapter = new QuickAdapter<ListBean>(activity, R.layout.item_vr_gallery) {
//            @Override
//            protected void convert(BaseAdapterHelper helper, ListBean item) {
//                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv);
//
//                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
//                helper.setImageUrl(R.id.iv, item.getImg().getUrl());
//                String displayname = item.getName();
//                if (bLocal) {
//                    String surfix = "";
//                    if (item.getCurState() == ListBean.STATE_DOWNLOADING) {
//                        surfix = (int) (item.getCurrent() * 100 / item.getTotal()) + "%";
//                    } else if (item.getCurState() == ListBean.STATE_SUCCESS) {
//                        surfix = "下载完成";
//                    } else if (item.getCurState() == ListBean.STATE_FAILED) {
//                        surfix = "下载失败";
//                    }
//                    displayname += surfix;
//                }
//                helper.setText(R.id.tv, displayname);
//            }
//        };

    }

    @Override
    public void init() {
        if (bFirstInit) {

        }
        super.init();
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        L.e("onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public void hideTextView() {
        this.getRootView().findViewById(R.id.tv_page).setVisibility(View.GONE);
        this.getRootView().findViewById(R.id.tv_title).setVisibility(View.GONE);
        this.getRootView().findViewById(R.id.tv_desc).setVisibility(View.GONE);
        bt_ceach.setVisibility(View.GONE);
        this.getRootView().findViewById(R.id.tv_fanhui).setVisibility(View.GONE);
    }

    public void setPageCenter() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) id_sv.getLayoutParams();
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.setMargins(0, 0, 0, DensityUtils.dp2px(activity, 100));
        id_sv.setLayoutParams(params);
        ImageView iv_cancel = (ImageView) this.getRootView().findViewById(R.id.iv_cancel);
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) iv_cancel.getLayoutParams();
        params1.setMargins(0, 0, 0, 0);
    }

}
