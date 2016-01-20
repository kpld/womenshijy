package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/11/29.
 */
public class Help {
//    标题介绍
    String title;
//    图片路径
    String image_url;

    @Override
    public String toString() {
        return "Help{" +
                "title='" + title + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
