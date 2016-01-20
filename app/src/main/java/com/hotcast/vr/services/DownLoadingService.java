package com.hotcast.vr.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.LocalBean;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownLoadingService extends Service {
    HttpUtils httpUtils;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    Map<String, HttpHandler> handlers;
    private DbUtils db;
    DownLoadingReceiver receiver;

    public DownLoadingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new DownLoadingReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        httpUtils = new HttpUtils();
        handlers = new HashMap<>();
        db = DbUtils.create(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


    }
    Details details;
    String localUrl;
    String play_url;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownLoadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(START) && BaseApplication.detailsList.size() > 0) {
//                    Details details = (Details) intent.getSerializableExtra("Details");
                details = BaseApplication.detailsList.get(0);
                localUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/" + details.getTitle() + ".mp4";
//                final String play_url = intent.getStringExtra("play_url");
                play_url = BaseApplication.playUrls.get(0);
                System.out.println("---服务广播下载" + play_url);
                HttpUtils httpUtils = new HttpUtils();
                HttpHandler handler = httpUtils.download(play_url, localUrl, true, true, new RequestCallBack<File>() {
                    String nowurl = play_url;
                    String localurl = localUrl;
                    Details nowDetali = details;

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        BaseApplication.playUrls.remove(nowurl);
                        BaseApplication.detailsList.remove(nowDetali);
                        Intent intent = new Intent(FINISH);
                        intent.putExtra("play_url", nowurl);
                        intent.putExtra("localurl", localurl);
                        sendBroadcast(intent);
                        Intent intent2 = new Intent(START);
                        sendBroadcast(intent2);
                        try {
                            LocalBean localBean = db.findById(LocalBean.class, nowurl);
                            if (localBean != null) {
                                //状态更新
//                                db.delete(localBean);
                                localBean.setCurState(3);
                                localBean.setLocalurl(localurl);
                                db.saveOrUpdate(localBean);
//                                db.save(localBean);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        BaseApplication.playUrls.remove(nowurl);
                        BaseApplication.detailsList.remove(nowDetali);
                    }

                    @Override
                    public void onStart() {
                        System.out.print("---开始下载，本地地址为：" + localurl);
                        Intent intent = new Intent(START+"LocalCache");
                        intent.putExtra("play_url", nowurl);
                        intent.putExtra("localurl", localurl);
                        sendBroadcast(intent);
                        LocalBean localBean = null;
                        try {
                            localBean = db.findById(LocalBean.class, nowurl);
                            System.out.print("---数据库更新localBean：" + localBean);
                            if (localBean != null) {
                                //状态更新
//                                db.delete(localBean);
                                localBean.setLocalurl(localurl);
                                localBean.setCurState(1);
                                db.saveOrUpdate(localBean);
//                                db.save(localBean);
                            }
                        } catch (DbException e) {
                            System.out.print("---数据库更新失败localBean：" + localBean);
                            e.printStackTrace();
                        }
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        Intent intent = new Intent(DOWNLOADING);
                        intent.putExtra("total", total);
                        intent.putExtra("current", current);
                        intent.putExtra("play_url", play_url);
                        System.out.println("---服务广播下载---" + localUrl);
                        sendBroadcast(intent);
//                            Toast.makeText(context,current+"/"+total,Toast.LENGTH_SHORT).show();
                    }
                });
                handlers.put(play_url, handler);
            } else if (intent.getAction().equals(PAUSE)) {
                    String play_url = intent.getStringExtra("play_url");
                    HttpHandler handler = handlers.get(play_url);
                //停止下載
                if (handler != null) {
                    handler.cancel();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    class DownThread extends Thread {
        @Override
        public void run() {

        }
    }
}
