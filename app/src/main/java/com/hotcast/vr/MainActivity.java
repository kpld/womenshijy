package com.hotcast.vr;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.bean.ListBean;
import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.pageview.BaseView;
import com.hotcast.vr.pageview.Grid3dView;
import com.hotcast.vr.pageview.List3dView;
import com.hotcast.vr.pageview.ListLocalView;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.NetUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity {


    @InjectView(R.id.tabContainer)
    LinearLayout tabContainer;
    @InjectView(R.id.pager)
    ViewPager pager;

    private MyPagerAdapter adapter;

    private View []tabViews = new View[4];
    private int curTabIndex = -1;

    private BaseView view0, view1, view2, view3;
    private BaseView[] views = new BaseView[4];
    private DownloadManager downloadManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        initTab();
        view0 = new List3dView(this, 0);
        view1 = new List3dView(this, 1);
        view2 = new Grid3dView(this, 2);
        view3 = new ListLocalView(this);
        views[0] = view0;
        views[1] = view1;
        views[2] = view2;
        views[3] = view3;

        List<View> vs = new ArrayList<View>();
        vs.add(view0.getRootView());
        vs.add(view1.getRootView());
        vs.add(view2.getRootView());
        vs.add(view3.getRootView());
        adapter = new MyPagerAdapter(vs);

        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                clickTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        pager.setCurrentItem(0);
        clickTab(0);

    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(NetUtils.isConnected(this) && !NetUtils.isWifi(this)){
            showToast("您正在使用移动网络");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

//    public void downloadMedia(ListBean bean){
//        String url = bean.getUrls();
//        String name = bean.getName();
//        L.e("mediaId\t"+bean.getId()+"\nurl\t"+url+"\nname\t"+bean.getName());
//        if(!MediaDownloadManager.isExsited(this, bean)) {
//            showToast(bean.getName()+"开始下载");
//            //开始下载
//            Uri resource = Uri.parse(encodeGB(url));
//            DownloadManager.Request request = new DownloadManager.Request(resource);
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//            request.setAllowedOverRoaming(false);
//            //设置文件类型
//            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
//            request.setMimeType(mimeString);
//            //在通知栏中显示
//            request.setShowRunningNotification(true);
//            request.setVisibleInDownloadsUi(true);
//            //sdcard的目录下的download文件夹
////            String[] surfix = url.split("\\.");
//            String filename = Utils.getFileName(url);
//            L.e("download filename =" + filename);
//            String folder = "/jarvis/download/";
//            request.setDestinationInExternalPublicDir(folder, filename);
//            request.setTitle(name);
//            bean.setLocalPath(Environment.getExternalStorageDirectory().getAbsolutePath()+folder+filename);
//            long id = downloadManager.enqueue(request);
//            bean.setDownloadId(id);
//            MediaDownloadManager.add(this, bean);
//            //保存id
////            prefs.edit().putLong(mediaId, id).commit();
//        } else {
//            //下载已经开始，检查状态
//            queryDownloadStatus(bean.getDownloadId());
//        }
//
////        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }
    public void downloadMedia(final ListBean bean){
        String url = bean.getUrl();
        String name = bean.getName();
        L.e("mediaId\t"+bean.getId()+"\nurl\t"+url+"\nname\t"+bean.getName());
        if(!MediaDownloadManager.isExsited(this, bean) || MediaDownloadManager.getState(this, bean) == ListBean.STATE_FAILED) {
            showToast(bean.getName() + "开始下载");
            //开始下载
            String folder = "/jarvis/download/";
            bean.setLocalPath(Environment.getExternalStorageDirectory().getAbsolutePath() + folder + bean.getId());
            File f = new File(bean.getLocalPath());
            if(f.exists()){
                f.delete();
            }
            HttpUtils http = new HttpUtils();
            HttpHandler handler = http.download(bean.getUrl(), bean.getLocalPath(), true, false, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                    super.onStart();
                    bean.setCurState(ListBean.STATE_DOWNLOADING);
                    MediaDownloadManager.updated(getApplicationContext(), bean);

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
//                    L.e("total="+total+"\tcurrent="+current+"\tisuploading="+isUploading);
                    super.onLoading(total, current, isUploading);
                    bean.setCurState(ListBean.STATE_DOWNLOADING);
                    bean.setTotal(total);
                    bean.setCurrent(current);
                    MediaDownloadManager.updated(getApplicationContext(), bean);
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    bean.setCurState(ListBean.STATE_SUCCESS);
                    MediaDownloadManager.updated(getApplicationContext(), bean);

                }

                @Override
                public void onFailure(HttpException e, String s) {
                    bean.setCurState(ListBean.STATE_FAILED);
                    MediaDownloadManager.updated(getApplicationContext(), bean);

                }
            });
//            bean.setDownloadId(id);
            MediaDownloadManager.add(this, bean);
            //保存id
//            prefs.edit().putLong(mediaId, id).commit();
        } else {
            //下载已经开始，检查状态
//            queryDownloadStatus(bean.getDownloadId());
        }

//        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * @param string
     * @return
     */
    public String encodeGB(String string)
    {
        //转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0]+"/"+split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
//            long downloadId = +intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//            L.v("intent", "" + downloadId);
////            queryDownloadStatus();
//            queryDownloadStatus(downloadId);
//        }
//    };


//    private void queryDownloadStatus(long downloadId) {
//        DownloadManager.Query query = new DownloadManager.Query();
//        query.setFilterById(downloadId);
//        Cursor c = downloadManager.query(query);
//        if(c.moveToFirst()) {
//            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            switch(status) {
//                case DownloadManager.STATUS_PAUSED:
//                    L.v("down", "STATUS_PAUSED");
//                case DownloadManager.STATUS_PENDING:
//                    L.v("down", "STATUS_PENDING");
//                case DownloadManager.STATUS_RUNNING:
//                    //正在下载，不做任何事情
//                    L.v("down", "STATUS_RUNNING");
//                    showToast("影片下载中...");
//                    break;
//                case DownloadManager.STATUS_SUCCESSFUL:
//                    MediaDownloadManager.setDownloaded(this, downloadId, true);
//                    //完成
//                    L.v("down", "下载完成");
//                    break;
//                case DownloadManager.STATUS_FAILED:
//                    //清除已下载的内容，重新下载
//                    L.v("down", "STATUS_FAILED");
//                    downloadManager.remove(downloadId);
//                    MediaDownloadManager.del(this, downloadId);
//                    break;
//            }
//        }
//    }

    @Override
    public void getIntentData(Intent intent) {

    }

    private void setTabText(View v, String tab){
        TextView tv = ButterKnife.findById(v, R.id.tv);
        tv.setText(tab);
    }

    private void setTabStatus(View v, boolean on){
        View line = ButterKnife.findById(v, R.id.line);
        TextView tv = ButterKnife.findById(v, R.id.tv);
        if (on){
            line.setVisibility(View.VISIBLE);
            tv.setTextColor(getResources().getColor(R.color.bright_word_color));
        }else{
            line.setVisibility(View.INVISIBLE);
            tv.setTextColor(getResources().getColor(R.color.dark_word_color));
        }
    }

    private void initTab(){
        for(int i=0; i<4; i++){
            tabViews[i] = tabContainer.getChildAt(i);
            switch (i){
                case 0:
                    setTabText(tabViews[i], "全景");
                    break;
                case 1:
                    setTabText(tabViews[i], "3D");
                    break;
                case 2:
                    setTabText(tabViews[i], "IMAX");
                    break;
                case 3:
                    setTabText(tabViews[i], "本地");
                    break;
            }
        }

        for(int i=0; i<tabViews.length; i++){
            final int index = i;
            tabViews[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pager.setCurrentItem(index);
//                    clickTab(index);
                }
            });
        }
    }

    private void clickTab(int index){
        if(index != curTabIndex){
            changeTabIndex(index);
            changeViewIndex(index);
            curTabIndex = index;
        }
    }

    private void changeTabIndex(int index){
        for(int i=0; i<tabViews.length; i++){
            boolean on;
            if(i == index){
                on = true;
            }else{
                on = false;
            }
            setTabStatus(tabViews[i], on);
        }
    }

    private void changeViewIndex(int index){
        views[index].init();


//        viewContainer.removeAllViews();
//        for(int i=0; i<views.length; i++){
//            if(i == index){
//                viewContainer.addView(views[i].getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                views[i].init();
//                break;
//            }
//        }
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
}