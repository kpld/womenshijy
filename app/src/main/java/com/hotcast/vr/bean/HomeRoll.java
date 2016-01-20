package com.hotcast.vr.bean;


/**
 * Created by lostnote on 15/11/29.
 */
public class HomeRoll {
    //标题
    String title;
    //海报地址
    String image;
    //点击操作，有三种值，urls：web，one：只有一个视频的节目集，many：表示有多个视频的节目集
    String action;
    //跳转地址
    String url;
    //资源ID
    String resource;
    //展示次数
    String show_times;
    //简单评价
    String desc;
    int id;
    String channel_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String reesource) {
        this.resource = resource;
    }

    public String getShow_times() {
        return show_times;
    }

    public void setShow_times(String show_times) {
        this.show_times = show_times;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "HomeRoll{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", action='" + action + '\'' +
                ", urls='" + url + '\'' +
                ", recource='" + resource + '\'' +
                ", show_times='" + show_times + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
