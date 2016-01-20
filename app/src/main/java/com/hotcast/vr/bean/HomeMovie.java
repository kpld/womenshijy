package com.hotcast.vr.bean;

public class HomeMovie {

    String name;//视频标题
    String desc;//视频简介
    String mode;//视频模式
    String url;//网址
    String media_id;//视频Id
    String show_times;//播放次数
    String images;//不同分辨率的预览图

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public void setShow_times(String show_times) {
        this.show_times = show_times;
    }


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getMode() {
        return mode;
    }

    public String getUrl() {
        return url;
    }

    public String getMedia_id() {
        return media_id;
    }

    public String getShow_times() {
        return show_times;
    }





}
