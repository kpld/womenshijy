package com.hotcast.vr.tools;

import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/1/13.
 */
public class HotVedioCacheUtils {
    private static String[] fileList;

    /**
     * @param cacheUrl 缓存路径
     * @return 返回MP4文件名称的数组
     */
    public static String[] getVedioCache(String cacheUrl) {
        File cacheFile = new File(cacheUrl);
        if (cacheFile.exists()) {
            if (!cacheFile.isDirectory()) {
                Log.w("HotVedioCacheUtils", "缓存路径输入错误(这不是一个文件夹)");
            } else {
                fileList = cacheFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".mp4") || filename.endsWith(".MP4")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        }
        return fileList;
    }
}
