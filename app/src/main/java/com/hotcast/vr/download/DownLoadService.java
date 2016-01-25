package com.hotcast.vr.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.download.dbcontrol.bean.SQLDownLoadInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

/**
 * 类功能描述：下载器后台服务</br>
 *
 * @author zhuiji7  (470508081@qq.com)
 * @version 1.0
 *          </p>
 */

public class DownLoadService extends Service {
    private static DownLoadManager downLoadManager;
    DbUtils db;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public static DownLoadManager getDownLoadManager() {
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
        BaseApplication.downLoadManager = downLoadManager;
        db = DbUtils.create(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放downLoadManager
        downLoadManager.stopAllTask();
        downLoadManager = null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (downLoadManager == null) {
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
    }

    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    private class DownloadManagerListener implements DownLoadListener {
        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            System.out.println("---接收到刷新信息onStart");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean localBean = db.findById(LocalBean.class,taskID);
                localBean.setCurState(1);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl+localBean.getTitle()+".mp4");
                localBean.setDownloading(true);
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            //根据监听到的信息查找列表相对应的任务，更新相应任务的进度
            System.out.println("---onProgress");
            Intent intent = new Intent(DOWNLOADING);
            intent.putExtra("play_url",sqlDownLoadInfo.getTaskID());
            intent.putExtra("current",sqlDownLoadInfo.getDownloadSize());
            intent.putExtra("total",sqlDownLoadInfo.getFileSize());
            sendBroadcast(intent);

        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            System.out.println("---接收到刷新信息onSuccess");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean localBean = db.findById(LocalBean.class,taskID);
                localBean.setCurState(3);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl+localBean.getTitle()+".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，停止该任务
            System.out.println("---接收到刷新信息onError");
            String taskID = sqlDownLoadInfo.getTaskID();//taskID是网络地址
            try {
                LocalBean localBean = db.findById(LocalBean.class,taskID);
                localBean.setCurState(2);
                localBean.setLocalurl(BaseApplication.VedioCacheUrl+localBean.getTitle()+".mp4");
                db.saveOrUpdate(localBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }


}
