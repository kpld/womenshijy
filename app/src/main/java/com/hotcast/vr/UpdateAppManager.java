package com.hotcast.vr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hotcast.vr.tools.L;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * Created by lostnote on 15/12/8.
 */
public class UpdateAppManager {
    private Context context;
    // 更新应用版本标记
    private static final int UPDARE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;
    private static final int STOP = 0;
    HttpHandler httpHandler;


    private String message = "检测新版本发布，建议您更新！";
    // 下载路径
    private String spec;
    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private ProgressBar progressBar;
    // 进度条的当前刻度值
    private int curProgress;
    // 用户是否取消下载
    private boolean isCancel;
    private int force;
    private String newFeatures;

    public UpdateAppManager(Context context, String spec, int force,String newFeatures) {
        this.context = context;
        this.spec = spec;
        this.force = force;
        this.newFeatures = newFeatures;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    progressBar.setProgress(curProgress);
//                    System.out.println("***正在下载");
                    break;

                case INSTALL_TOKEN:
                    installApp();
                    break;
//                case STOP:

            }
        }
    };

    /**
     * 检测应用更新信息
     */
    public void checkUpdateInfo() {
        switch (force) {
            case 0://不强制更新
                showNoticeDialog(newFeatures);
                break;
            case 1://强制更新
                showDownloadDialog();
                break;
        }
    }

    /**
     * 显示提示更新对话框
     */
    private void showNoticeDialog(String newFeatures) {
        new AlertDialog.Builder(context)
                .setTitle("软件版本更新")
                .setMessage(message + newFeatures)
                .setPositiveButton("下载", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDownloadDialog();
                    }
                }).setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 显示下载进度对话框
     */
    private void showDownloadDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.progressbar, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("软件版本更新");
        builder.setView(view);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isCancel = true;
            }
        });
        dialog = builder.create();
        dialog.show();
        downloadApp();
    }

    /**
     * 下载新版本应用
     */
    public void downloadApp() {
//         多线程断点下载。

        final HttpUtils http = new HttpUtils();
        httpHandler = http.download(spec, "/mnt/sdcard/VR热播.apk", new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                System.out.println("安装 /mnt/sdcard/VR热播.apk");
                handler.sendEmptyMessage(INSTALL_TOKEN);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                System.out.println(arg1);
                arg0.printStackTrace();
//                        loadMainUI();
            }

            @Override
            public void onLoading(long total, long current,
                                  boolean isUploading) {
                if (isCancel){
                    httpHandler.cancel();
                }
//                        tv_info.setText(current + "/" + total);
                L.e("----正在下载：" + current + "/" + total);
                curProgress = (int) (((float) current / total) * 100);
                System.out.println(curProgress);
                handler.sendEmptyMessage(UPDARE_TOKEN);
                super.onLoading(total, current, isUploading);
            }
        });
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "VR热播.apk")), "application/vnd.android.package-archive");//编者按：此处Android应为android，否则造成安装不了
        context.startActivity(intent);

    }

    public HttpHandler downloadAppInlandscape(final Handler mHandler) {
//         多线程断点下载。

        final HttpUtils http = new HttpUtils();
        httpHandler = http.download(spec, "/mnt/sdcard/VR热播.apk", new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                System.out.println("安装 /mnt/sdcard/VR热播.apk");
                mHandler.sendEmptyMessage(INSTALL_TOKEN);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                System.out.println(arg1);
                arg0.printStackTrace();
//                        loadMainUI();
            }

            @Override
            public void onLoading(long total, long current,
                                  boolean isUploading) {
                if (isCancel){
                    httpHandler.cancel();
                }
//                        tv_info.setText(current + "/" + total);
                L.e("----正在下载：" + current + "/" + total);
                curProgress = (int) (((float) current / total) * 100);
                System.out.println(curProgress);
                Message msg = Message.obtain();
                msg.obj = curProgress;
                msg.what = UPDARE_TOKEN;
                mHandler.sendMessage(msg);
                super.onLoading(total, current, isUploading);
            }
        });
        return httpHandler;
    }
}
