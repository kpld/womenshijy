package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/11/29.
 */
public class Play {
    //标题
    String title;
    //标清源
    String sd_url;
    //高清源
    String hd_url;
    //超清源
    String uhd_url;
    //web播放地址（最低清晰度)
    String web_url;

    String imgurl;//预览图

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSd_url() {
        return sd_url;
    }

    public void setSd_url(String sd_url) {
        this.sd_url = sd_url;
    }

    public String getHd_url() {
        return hd_url;
    }

    public void setHd_url(String hd_url) {
        this.hd_url = hd_url;
    }

    public String getUhd_url() {
        return uhd_url;
    }

    public void setUhd_url(String uhd_url) {
        this.uhd_url = uhd_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    @Override
    public String toString() {
        return "Play{" +
                "title='" + title + '\'' +
                ", sd_url='" + sd_url + '\'' +
                ", hd_url='" + hd_url + '\'' +
                ", uhd_url='" + uhd_url + '\'' +
                ", web_url='" + web_url + '\'' +
                '}';
    }
}
