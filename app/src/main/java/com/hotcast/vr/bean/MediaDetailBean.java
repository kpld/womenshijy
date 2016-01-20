package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by joey on 8/5/15.
 */
public class MediaDetailBean {

    /**
     * cover : {"width":220,"suffix":"d","urls":"http://101.200.231.61:8001/cover/5b/322274d.jpg","height":360}
     * score : 90
     * channel : movie
     * name : 生命之旅
     * format : hd
     * description : 多亏了医学照相术的发展，这部记录片给观众呈现出一段关于世界起源的全新的旅程；影片叙述了诞生在人体内的生命起源的最艰难的9个月。让观众看清两个“宇宙”的诞生：一个是婴儿从孕育到成长的过程，还有它赖以生存的子宫世界；另一个是怀孕的母亲，将她这9个月里面每日的艰难时刻以及复杂的情感用一个母性的声音展现出来。
     * id : f78Vqh5St2R
     * age : 2006
     * episodes : [{"urls":[{"op":0,"urltype":0,"format":"hd","type":"hd","urls":"http://pl.youku.com/playlist/m3u8?vid=31371026&type=mp4&ts=1437762904&keyframe=1&ep=cyaXHU6LVMcA4SDagD8bMS6xdnRbXP0L9RiEgdBkb7IkTu8%3D&sid=143776290435812f2c23d&token=9067&ctype=12&ev=1&oip=1707651655","ed":0},{"op":0,"urltype":0,"format":"sd","type":"sd","urls":"http://pl.youku.com/playlist/m3u8?vid=31371026&type=flv&ts=1437762904&keyframe=1&ep=cyaXHU6LVMcA4SDagD8bMS6xdnRbXP0L9RiEgdBkb7IkTu8%3D&sid=143776290435812f2c23d&token=9067&ctype=12&ev=1&oip=1707651655","ed":0}],"name":"优酷","logo":{"width":156,"urls":"http://101.200.231.61:8001/logos/youku.png","height":39},"source":"http://v.youku.com/v_show/id_XMTI1NDg0MTA0.html"},{"urls":[{"op":0,"urltype":0,"format":"hd","type":"hd","urls":"http://pl.youku.com/playlist/m3u8?vid=XMTI1NDg0MTA0&type=mp4&ts=1437760256&keyframe=1&ep=diaXHU6LVswF4CrbgT8bYiTgdiIHXJZ3kmaE%2F6Y1AMZAOejQnzjSzg%3D%3D&sid=4437760255949125822e8&token=6639&ctype=12&ev=1&oip=1707651655","ed":0},{"op":0,"urltype":0,"format":"sd","type":"sd","urls":"http://pl.youku.com/playlist/m3u8?vid=XMTI1NDg0MTA0&type=flv&ts=1437760256&keyframe=1&ep=diaXHU6LVswF4CrbgT8bYiTgdiIHXJZ3kmaE%2F6Y1AMZAOejQnzjSzg%3D%3D&sid=4437760255949125822e8&token=6639&ctype=12&ev=1&oip=1707651655","ed":0}],"name":"土豆","logo":{"width":136,"urls":"http://101.200.231.61:8001/logos/tudou.png","height":50},"source":"http://www.tudou.com/albumplay/L5AOekRxEm4/moAPeY0QfLE.html"}]
     */
    private CoverEntity cover;
    private int score;
    private String channel;
    private String name;
    private String format;
    private String description;
    private String id;
    private String age;
    private List<EpisodesEntity> episodes;

    public void setCover(CoverEntity cover) {
        this.cover = cover;
    }

    public void setScore(int score) {
        this.score = score;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setEpisodes(List<EpisodesEntity> episodes) {
        this.episodes = episodes;
    }

    public CoverEntity getCover() {
        return cover;
    }

    public int getScore() {
        return score;
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

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getAge() {
        return age;
    }

    public List<EpisodesEntity> getEpisodes() {
        return episodes;
    }

    public static class CoverEntity {
        /**
         * width : 220
         * suffix : d
         * urls : http://101.200.231.61:8001/cover/5b/322274d.jpg
         * height : 360
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

    public static class EpisodesEntity {
        /**
         * urls : [{"op":0,"urltype":0,"format":"hd","type":"hd","urls":"http://pl.youku.com/playlist/m3u8?vid=31371026&type=mp4&ts=1437762904&keyframe=1&ep=cyaXHU6LVMcA4SDagD8bMS6xdnRbXP0L9RiEgdBkb7IkTu8%3D&sid=143776290435812f2c23d&token=9067&ctype=12&ev=1&oip=1707651655","ed":0},{"op":0,"urltype":0,"format":"sd","type":"sd","urls":"http://pl.youku.com/playlist/m3u8?vid=31371026&type=flv&ts=1437762904&keyframe=1&ep=cyaXHU6LVMcA4SDagD8bMS6xdnRbXP0L9RiEgdBkb7IkTu8%3D&sid=143776290435812f2c23d&token=9067&ctype=12&ev=1&oip=1707651655","ed":0}]
         * name : 优酷
         * logo : {"width":156,"urls":"http://101.200.231.61:8001/logos/youku.png","height":39}
         * source : http://v.youku.com/v_show/id_XMTI1NDg0MTA0.html
         */
        private List<UrlsEntity> urls;
        private String name;
        private LogoEntity logo;
        private String source;

        public void setUrls(List<UrlsEntity> urls) {
            this.urls = urls;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLogo(LogoEntity logo) {
            this.logo = logo;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public List<UrlsEntity> getUrls() {
            return urls;
        }

        public String getName() {
            return name;
        }

        public LogoEntity getLogo() {
            return logo;
        }

        public String getSource() {
            return source;
        }

        public static class UrlsEntity {
            /**
             * op : 0
             * urltype : 0
             * format : hd
             * type : hd
             * urls : http://pl.youku.com/playlist/m3u8?vid=31371026&type=mp4&ts=1437762904&keyframe=1&ep=cyaXHU6LVMcA4SDagD8bMS6xdnRbXP0L9RiEgdBkb7IkTu8%3D&sid=143776290435812f2c23d&token=9067&ctype=12&ev=1&oip=1707651655
             * ed : 0
             */
            private int op;
            private int urltype;
            private String format;
            private String type;
            private String url;
            private int ed;

            public void setOp(int op) {
                this.op = op;
            }

            public void setUrltype(int urltype) {
                this.urltype = urltype;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public void setEd(int ed) {
                this.ed = ed;
            }

            public int getOp() {
                return op;
            }

            public int getUrltype() {
                return urltype;
            }

            public String getFormat() {
                return format;
            }

            public String getType() {
                return type;
            }

            public String getUrl() {
                return url;
            }

            public int getEd() {
                return ed;
            }
        }

        public static class LogoEntity {
            /**
             * width : 156
             * urls : http://101.200.231.61:8001/logos/youku.png
             * height : 39
             */
            private int width;
            private String url;
            private int height;

            public void setWidth(int width) {
                this.width = width;
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

            public String getUrl() {
                return url;
            }

            public int getHeight() {
                return height;
            }
        }
    }
}
