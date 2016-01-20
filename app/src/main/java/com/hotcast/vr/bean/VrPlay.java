package com.hotcast.vr.bean;

import java.io.Serializable;

/**
 * Created by lostnote on 15/12/11.
 */
public class VrPlay implements Serializable {
    String title;
    String image;
    String resource;
    String desc;
    String video_url;
    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

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
        return "VrPlay{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", resource='" + resource + '\'' +
                ", desc='" + desc + '\'' +
                ", video_url='" + video_url + '\'' +
                '}';
    }
}
