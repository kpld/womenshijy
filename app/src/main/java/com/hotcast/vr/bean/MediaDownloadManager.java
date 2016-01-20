package com.hotcast.vr.bean;

import android.content.Context;

import com.hotcast.vr.tools.LocalStorage;
import com.hotcast.vr.tools.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 8/11/15.
 */
public class MediaDownloadManager {
    private static final String KEY_DOWNLOADED_MEDIA = "key_downloaded_media";

    public static void setDownloaded(Context context, long id, boolean download){
        List<ListBean> list = getAll(context);
        for (ListBean tmpBean : list){
            if(tmpBean.getDownloadId() == id){
//                tmpBean.setDownloadSuccessed(download);
                tmpBean.setCurState(ListBean.STATE_SUCCESS);
                break;
            }
        }

        byte[] bytes = Utils.obj2ByteArray(list);
        LocalStorage.getIntance(context).setByteItem(KEY_DOWNLOADED_MEDIA, bytes);
    }

    public static boolean isDownloading(Context context){
        List<ListBean> list = getAll(context);
        boolean isDownloading = false;
        for (ListBean tmpBean : list){
            if(tmpBean.getCurState() == ListBean.STATE_DOWNLOADING){
                isDownloading = true;
                break;
            }
        }
        return isDownloading;
    }

    public static int getState(Context context, ListBean bean){
        List<ListBean> list = getAll(context);
        int state = ListBean.STATE_NONE;
        for (ListBean tmpBean : list){
            if(tmpBean.getId().equals(bean.getId())){
                state = tmpBean.getCurState();
                break;
            }
        }
        return state;
    }

    public static boolean isExsited(Context context, ListBean bean){
        List<ListBean> list = getAll(context);
        boolean existed = false;
        for (ListBean tmpBean : list){
            if(tmpBean.getId().equals(bean.getId())){
                existed = true;
                break;
            }
        }
        return existed;
    }

    public static void updated(Context context, ListBean bean){
        List<ListBean> list = getAll(context);
        for(ListBean tmpBean : list){
            if(tmpBean.getId().equals(bean.getId())){
                tmpBean.setCurrent(bean.getCurrent());
                tmpBean.setTotal(bean.getTotal());
                tmpBean.setCurState(bean.getCurState());
                break;
            }
        }
        byte[] bytes = Utils.obj2ByteArray(list);
        LocalStorage.getIntance(context).setByteItem(KEY_DOWNLOADED_MEDIA, bytes);
    }

    public static void del(Context context, long downloadid){
        List<ListBean> list = getAll(context);
        for(ListBean tmpBean : list){
            if(tmpBean.getDownloadId() == downloadid){
                list.remove(tmpBean);
                break;
            }
        }
        byte[] bytes = Utils.obj2ByteArray(list);
        LocalStorage.getIntance(context).setByteItem(KEY_DOWNLOADED_MEDIA, bytes);
    }

    public static void  add(Context context, ListBean bean){
        if(!isExsited(context, bean)){
            List<ListBean> list = getAll(context);
            list.add(0, bean);
            byte[] bytes = Utils.obj2ByteArray(list);
            LocalStorage.getIntance(context).setByteItem(KEY_DOWNLOADED_MEDIA, bytes);
        }
    }

    public static List<ListBean> getAll(Context context){

        byte[] bytes = LocalStorage.getIntance(context).getByteItem(KEY_DOWNLOADED_MEDIA);
        if(null == bytes){
            return new ArrayList<ListBean>();
        }
        List<ListBean> list = (List<ListBean>) Utils.byteArray2Object(bytes);
        if(null == list){
            return new ArrayList<ListBean>();
        }

        return list;
    }
}
