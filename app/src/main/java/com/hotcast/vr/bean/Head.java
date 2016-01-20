package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/12/4.
 */
public class Head {
    //        栏目图片URL
    String image;
    //        栏目名称
    String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Head{" +
                "image='" + image + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
