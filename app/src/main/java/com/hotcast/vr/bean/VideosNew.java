package com.hotcast.vr.bean;

import java.io.Serializable;

/**
 * Created by lostnote on 16/1/28.
 */
public class VideosNew implements Serializable{
    //视频ID（字符串）
    String vid;
    //视频名称
    String vname;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    @Override
    public String toString() {
        return "VideosNew{" +
                "vid='" + vid + '\'' +
                ", vname='" + vname + '\'' +
                '}';
    }
}
