package com.hotcast.vr.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.tools.L;

/**
 * Created by joey on 8/12/15.
 */
public class DownloadReceiver extends BroadcastReceiver {



    private DownloadManager downloadManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
        long downloadId = +intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        L.v("intent", "" + downloadId);
//            queryDownloadStatus();
        queryDownloadStatus(context, downloadId);
    }


    private void queryDownloadStatus(Context context, long downloadId) {
        downloadManager = (DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if(c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                    L.v("down", "STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    L.v("down", "STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    //正在下载，不做任何事情
                    L.v("down", "STATUS_RUNNING");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    MediaDownloadManager.setDownloaded(context, downloadId, true);
                    //完成
                    L.v("down", "下载完成");
                    break;
                case DownloadManager.STATUS_FAILED:
                    //清除已下载的内容，重新下载
                    L.v("down", "STATUS_FAILED");
                    downloadManager.remove(downloadId);
                    MediaDownloadManager.del(context, downloadId);
                    break;
            }
        }
    }
}
