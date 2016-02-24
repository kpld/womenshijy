package com.hotcast.vr.bean;

import java.io.Serializable;

/**
 * Created by lostnote on 16/1/28.
 */
public class ChanelData implements Serializable{
    //频道ID（字符类型）
    String id;
    //频道标题
    String title;
    //图片
    String img;
    //变换图片
    String img_click;
    //大图
    String img_big;
    //显示效果
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_big() {
        return img_big;
    }

    public void setImg_big(String img_big) {
        this.img_big = img_big;
    }

    public String getImg_click() {
        return img_click;
    }

    public void setImg_click(String img_click) {
        this.img_click = img_click;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ChanelData{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", img='" + img + '\'' +
                ", img_click='" + img_click + '\'' +
                ", img_big='" + img_big + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
