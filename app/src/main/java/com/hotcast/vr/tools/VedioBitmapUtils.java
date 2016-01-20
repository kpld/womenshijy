package com.hotcast.vr.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.ViewGroup;

/**
 * Created by liurongzhi on 2016/1/13.
 */
public class VedioBitmapUtils {
    /**
     * @param path 视频的路径
     * kind 获得的缩略图的类型 ：MINI_KIND: 512 x 384
     */
    public static Bitmap getMiniVedioBitmap(String path) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        return bitmap;
    }

    /**
     * @param path 视频的路径
     * kind 获得的缩略图的类型 ：MICRO_KIND: 96 x 96
     */
    public static Bitmap getMicroVedioBitmap(String path) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
        return bitmap;
    }
    public static Bitmap extractThumbnail(Bitmap bp, int w, int h, Context context){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.extractThumbnail(bp,DensityUtils.dp2px(context,w) ,DensityUtils.dp2px(context,h) );
        return bitmap;
    }
}
