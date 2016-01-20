package com.hotcast.vr;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.tools.L;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;


public class BaseApplication extends Application {
    //    public static List<HomeRoll> homes;
    public static List<String> strs = new ArrayList<>();
    public static int size;
    public static boolean isDownLoad = false;
    public static boolean isUpdate = false;
    public static List<Classify> netClassifys;
    public static final String TAG = BaseApplication.class.getSimpleName();
    //    public static BitmapUtils mFinalBitmap;
    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public static final String IMG_DISCCACHE_DIR = "/mnt/sdcard/jarvis/imgcache";
    public static final String VedioCacheUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/";
    public static final String ImgCacheUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostcast/vr/vedioImg/";
    public static boolean pagerf = false;
    public static List<Classify> classifies = new ArrayList<>();
    public static List<String> playUrls = new ArrayList<>();//需要下載的電影地址
    public static List<Details> detailsList = new ArrayList<>();//需要下載的電影地址

    public static BitmapUtils getDisplay(Context context, int failedImgId) {
        BitmapUtils mFinalBitmap = new BitmapUtils(context, IMG_DISCCACHE_DIR);
        mFinalBitmap.configDefaultLoadFailedImage(failedImgId);
        mFinalBitmap.configDefaultLoadingImage(failedImgId);
        return mFinalBitmap;
    }


    @Override
    public void onCreate() {
        Thread.currentThread().setUncaughtExceptionHandler(new MyExecptionHandler());
        super.onCreate();

        instance = this;

        initMeta();
    }

    private class MyExecptionHandler implements Thread.UncaughtExceptionHandler {
        /**
         * 当线程出现了未捕获异常执行的方法
         * 不能阻止java虚拟机退出，只能在退出之前做点别的事
         *
         * @param thread
         * @param ex
         */
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            L.e("应用程序异常退出了");
            ex.printStackTrace();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void initMeta() {
        try {

            android.content.pm.ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null && appInfo.metaData != null) {
                L.mAddLog = appInfo.metaData.getBoolean("DEBUG");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public DisplayMetrics getDeviceDisplayMetrics(Context context) {
        android.view.WindowManager windowsManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = windowsManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }


}
