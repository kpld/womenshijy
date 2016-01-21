package com.hotcast.vr.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hotcast.vr.download.dbcontrol.bean.SQLDownLoadInfo;

/**
 * 类功能描述：下载器后台服务</br>
 *
 * @author zhuiji7  (470508081@qq.com)
 * @version 1.0
 * </p>
 */

public class DownLoadService extends Service {
    private static DownLoadManager  downLoadManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static DownLoadManager getDownLoadManager(){
        return downLoadManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadManager = new DownLoadManager(DownLoadService.this);
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
        if(downLoadManager == null){
            downLoadManager = new DownLoadManager(DownLoadService.this);
        }
        downLoadManager.setAllTaskListener(new DownloadManagerListener());
    }

    private class DownloadManagerListener implements DownLoadListener{

        @Override
        public void onStart(SQLDownLoadInfo sqlDownLoadInfo) {
            System.out.println("---接收到刷新信息onStart");
        }

        @Override
        public void onProgress(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {
            //根据监听到的信息查找列表相对应的任务，更新相应任务的进度
//            for(TaskInfo taskInfo : listdata){
//                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
//                    taskInfo.setDownFileSize(sqlDownLoadInfo.getDownloadSize());
//                    taskInfo.setFileSize(sqlDownLoadInfo.getFileSize());
//                    ListAdapter.this.notifyDataSetChanged();
//                    break;
//                }
//            }
            System.out.println("---onProgress");
        }

        @Override
        public void onStop(SQLDownLoadInfo sqlDownLoadInfo, boolean isSupportBreakpoint) {

        }

        @Override
        public void onSuccess(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，删除对应的任务
//            for(TaskInfo taskInfo : listdata){
//                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
//                    listdata.remove(taskInfo);
//                    ListAdapter.this.notifyDataSetChanged();
//                    System.out.println("---onSuccess");
//                    break;
//                }
//            }
        }

        @Override
        public void onError(SQLDownLoadInfo sqlDownLoadInfo) {
            //根据监听到的信息查找列表相对应的任务，停止该任务
//            for(TaskInfo taskInfo : listdata){
//                if(taskInfo.getTaskID().equals(sqlDownLoadInfo.getTaskID())){
//                    taskInfo.setOnDownloading(false);
//                    ListAdapter.this.notifyDataSetChanged();
//                    System.out.println("---onError");
//                    break;
//                }
//            }
        }
    }
    
    

}
