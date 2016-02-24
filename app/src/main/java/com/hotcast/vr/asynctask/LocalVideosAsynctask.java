package com.hotcast.vr.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.tools.HotVedioCacheUtils;
import com.hotcast.vr.tools.SaveBitmapUtils;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/2/2.
 */
public class LocalVideosAsynctask extends AsyncTask<Integer, Integer, List<LocalBean>>{
    Context context;
    private List<LocalBean> dbList;
    public LocalVideosAsynctask(Context context){
        super();
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List doInBackground(Integer... params) {
        System.out.println("---异步启动");
        DbUtils db = DbUtils.create(context);
        try {
            dbList = db.findAll(LocalBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbList == null) {
            dbList = new ArrayList<>();
        }
        if (dbList != null) {
            System.out.println("---数据库原始尺寸：" + dbList.size());
        }
        String[] localNames = HotVedioCacheUtils.getVedioCache(BaseApplication.VedioCacheUrl);
        if (localNames != null) {
            int size = dbList.size();
            List<String> titles = new ArrayList<>();
            for (LocalBean localBean : dbList){
                titles.add(localBean.getTitle());
            }
            for (int i = 0; i < localNames.length; i++) {
                String title = localNames[i];
                String title1 = title.replace(".mp4","");

                if (size == 0) {
                    System.out.println("---title1"+title1);
                    LocalBean localBean = new LocalBean();
                    localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                    localBean.setCurState(3);
                    SaveBitmapUtils.saveMyBitmap(title1, VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
//                        System.out.println("---数据库没有数据。添加本地bitmap：" + VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                    localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                    localBean.setUrl("");
                    localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
                    localBean.setTitle(title1);
//                    dbList.add(localBean);
                    try {
                        db.saveOrUpdate(localBean);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!titles.contains(title1)){
                        System.out.println("---不为空title1"+title1);
                        LocalBean localBean = new LocalBean();
                        localBean.setLocalurl(BaseApplication.VedioCacheUrl + localNames[i]);
                        localBean.setCurState(3);
                        SaveBitmapUtils.saveMyBitmap(title.replace(".mp4", ""), VedioBitmapUtils.getMiniVedioBitmap(BaseApplication.VedioCacheUrl + localNames[i]));
                        localBean.setImage(BaseApplication.ImgCacheUrl + title1 + ".jpg");
                        localBean.setUrl("");
                        localBean.setTitle(title1);
                        localBean.setId(BaseApplication.VedioCacheUrl + localNames[i]);
//                        dbList.add(localBean);
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        System.out.println("--"+title);
                    }
                }
            }
        }
        System.out.println("----初始化本地数据处理完毕");
        BaseApplication.doAsynctask = true;
        return dbList;
    }

    @Override
    protected void onPostExecute(List<LocalBean> s) {
//            super.onPostExecute(s);
    }
}

