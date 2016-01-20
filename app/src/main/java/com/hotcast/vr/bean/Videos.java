package com.hotcast.vr.bean;

/**
 * Created by lostnote on 15/12/3.
 */
public class Videos {
    @Override
    public String toString() {
        return "Videos{" +
                "video_id='" + video_id + '\'' +
                ", video_name='" + video_name + '\'' +
                '}';
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    String video_id;
    String video_name;
}
