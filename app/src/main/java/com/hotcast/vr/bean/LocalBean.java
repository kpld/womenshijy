package com.hotcast.vr.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by lostnote on 15/12/7.
 */
public class LocalBean implements Serializable {
    String DOWNLOADING = "DOWNLOADING";
    String FINISH = "FINISH";
    String PAUSE = "PAUSE";
    String WAITING = "WAITING";

    String title;
    String image;
    String url;
    Bitmap localBitmap;

    public Bitmap getLocalBitmap() {
        return localBitmap;
    }

    public void setLocalBitmap(Bitmap localBitmap) {
        this.localBitmap = localBitmap;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPecent() {
        return pecent;
    }

    public void setPecent(String pecent) {
        this.pecent = pecent;
    }

    String speed;
    String pecent;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String state = WAITING;

    String localurl;
    private boolean downloadSuccessed;

    public boolean isDownloadSuccessed() {
        return downloadSuccessed;
    }

    public void setDownloadSuccessed(boolean downloadSuccessed) {
        this.downloadSuccessed = downloadSuccessed;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public String getLocalurl() {
        return localurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    public static final int STATE_NONE = 0, STATE_DOWNLOADING = 1, STATE_FAILED = 2, STATE_SUCCESS = 3;
    private int curState = STATE_NONE;
    private long downloadId;
    private long total, current;

    public int getCurState() {
        return curState;
    }

    public void setCurState(int curState) {
        this.curState = curState;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public long getTotal() {

        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {

        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "LocalBean{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", curState=" + curState +
                ", downloadId=" + downloadId +
                ", total=" + total +
                ", current=" + current +
                '}';
    }
}
