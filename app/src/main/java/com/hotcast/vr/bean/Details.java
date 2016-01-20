package com.hotcast.vr.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lostnote on 15/11/29.
 */
public class Details implements Serializable{
//    标题
    String title;
//    海报地址
    String image;
//    简介
    String desc;
//    视频时长
    String video_length;
    //    上传时间
    String updated_at;
    public String getUpdate_at() {
        return updated_at;
    }

    public void setUpdate_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public ArrayList<Videos> getVideo() {
        return video;
    }

    public void setVideo(ArrayList<Videos> video) {
        this.video = video;
    }

    ArrayList<Videos> video;

    @Override
    public String toString() {
        return "Details{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", desc='" + desc + '\'' +
                ", video_length='" + video_length + '\'' +
                ", video=" + video +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVideo_length() {
        return video_length;
    }

    public void setVideo_length(String video_length) {
        this.video_length = video_length;
    }

}
