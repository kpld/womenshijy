package com.hotcast.vr.bean;


import java.io.Serializable;

/**
 * Created by lostnote on 15/11/29.
 */
public class Classify implements Serializable {
//    频道标题
    String title;
//    频道ID
    String channel_id;
//    展示方式
    String show_type;
//    横屏图片
    String image;
//    点击图片
    String image_click;
//    一体机使用的平道图片
    String big_logo;
    int id;

    public String getBig_logo() {
        return big_logo;
    }

    public void setBig_logo(String big_logo) {
        this.big_logo = big_logo;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_click() {
        return image_click;
    }

    public void setImage_click(String image_click) {
        this.image_click = image_click;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getShow_type() {
        return show_type;
    }

    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }

    @Override
    public String toString() {
        return "Classify{" +
                "title='" + title + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", show_type='" + show_type + '\'' +
                ", image='" + image + '\'' +
                ", image_click='" + image_click + '\'' +
                ", big_logo='" + big_logo + '\'' +
                ", id=" + id +
                '}';
    }
}
