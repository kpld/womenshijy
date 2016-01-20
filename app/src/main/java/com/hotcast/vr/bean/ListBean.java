package com.hotcast.vr.bean;

import java.io.Serializable;

/**
 * Created by joey on 8/4/15.
 */
public class ListBean implements Serializable{


    /**
     * score : 90
     * img : {"width":220,"suffix":"l","urls":"http://101.200.231.61:8001/cover/69/321905l.jpg","height":288}
     * channel : movie
     * name : 饥饿游戏3:嘲笑鸟(上)
     * format : shd
     * id : 27qyceUcHtF
     * released : 2018-11-21
     */
    private int score;
    private ImgEntity img;
    private String channel;
    private String name;
    private String format;
    private String id;
    private String released;
//    private boolean downloadSuccessed;
    public static final int STATE_NONE = 0, STATE_DOWNLOADING = 1, STATE_FAILED = 2, STATE_SUCCESS = 3;
    private int curState = STATE_NONE;
    private long downloadId;
    private String localPath;
    private String url;
    private long total, current;


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setImg(ImgEntity img) {
        this.img = img;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public int getScore() {
        return score;
    }

    public ImgEntity getImg() {
        return img;
    }

    public String getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public String getFormat() {
        return format;
    }

    public String getId() {
        return id;
    }

    public String getReleased() {
        return released;
    }

    public static class ImgEntity implements Serializable{
        /**
         * width : 220
         * suffix : l
         * urls : http://101.200.231.61:8001/cover/69/321905l.jpg
         * height : 288
         */
        private int width;
        private String suffix;
        private String url;
        private int height;

        public void setWidth(int width) {
            this.width = width;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public String getSuffix() {
            return suffix;
        }

        public String getUrl() {
            return url;
        }

        public int getHeight() {
            return height;
        }
    }
}
