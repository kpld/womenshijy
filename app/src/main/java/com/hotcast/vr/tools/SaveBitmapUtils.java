package com.hotcast.vr.tools;

import android.graphics.Bitmap;

import com.hotcast.vr.BaseApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liurongzhi on 2016/1/14.
 */
public class SaveBitmapUtils {
    public static void saveMyBitmap(String name, Bitmap mBitmap) {
        File dir = new File(BaseApplication.ImgCacheUrl);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(BaseApplication.ImgCacheUrl + name + ".jpg");
        if (!f.exists()) {
            try {
                System.out.println("----File:"+f);
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
