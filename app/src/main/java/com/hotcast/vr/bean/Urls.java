package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/12/3.
 */
public class Urls {
    //    HD清晰度地址
    String hd;
    //        SHD清晰度地址
    String shd;
    //        SD清晰度地址
    String sd;

    @Override
    public String toString() {
        return "Urls{" +
                "hd='" + hd + '\'' +
                ", shd='" + shd + '\'' +
                ", sd='" + sd + '\'' +
                '}';
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public String getShd() {
        return shd;
    }

    public void setShd(String shd) {
        this.shd = shd;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }


}
