package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/11/29.
 */
public class About {
//    图片地址
    String image_url;
//    logo地址
    String logo;
//    简介（内容）
    String desc;

    @Override
    public String toString() {
        return "About{" +
                "image_url='" + image_url + '\'' +
                ", logo='" + logo + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
