package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/11/29.
 */
public class Play {
//    标题
    String title;
//    播放路径集合
    Urls urls;

    @Override
    public String toString() {
        return "Play{" +
                "title='" + title + '\'' +
                ", urls=" + urls +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

}
