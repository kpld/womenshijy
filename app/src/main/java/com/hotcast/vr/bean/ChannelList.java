package com.hotcast.vr.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lostnote on 16/1/28.
 */
public class ChannelList implements Serializable {
    //节目集ID（字符串）
    String id;
    //标题
    String title;
    //不同分辨率尺寸的海报图
    List<String> image;
    //简介（一句话）
    String desc;
    //更新时间（时间戳）
    String update_time;
    //导演
    String director;
    //演员（字符串）
    String actors;
    //详情
    String description;
    List<VideosNew> videos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VideosNew> getVideos() {
        return videos;
    }

    public void setVideos(List<VideosNew> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "ChannelList{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", image=" + image +
                ", desc='" + desc + '\'' +
                ", update_time='" + update_time + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", description='" + description + '\'' +
                ", videos=" + videos +
                '}';
    }
}
