package com.hotcast.vr.tools;

public class Constants {
    //    地址前缀
    public static final String SERVER_URL = "http://api2.hotcast.cn/index.php?r=";
    //    频道列表
    public static final String CHANNEL_LIST = SERVER_URL + "/app/channel/get-list";
    //    频道下节目集列表
    public static final String PROGRAM_LIST = SERVER_URL + "/app/channel/get-video";
    //    节目详情
    public static final String DETAIL = SERVER_URL + "/app/videoset/get-detail";
    //    播放地址
    public static final String PLAY_URL = SERVER_URL + "/app/videoset/play";
    //    轮播推荐位
    public static final String ROLL = SERVER_URL + "/app/recommend/get-roll";
    //    专题推荐位
    public static final String SPECIAL = SERVER_URL + "/app/recommend/get-subjects";
    //    版本更新接口
    public static final String URL_UPDATE = SERVER_URL + "/app/info/update";
    public static final String RELATION = SERVER_URL + "/app/videoset/get-relation";



    //    public static final String SERVER = "http://101.200.231.61:9900";
//    地址前缀
    public static final String SERVER = "http://api.hotcast.cn/index.php?r=v20";
    //    public static final String URL_IMAX = SERVER + "/list";
//    首页地址接口
    public static final String URL_HOME = SERVER + "/home/get-page";
    //    分类菜单接口
    public static final String URL_CLASSIFY_TITLTE = SERVER + "/channel/get-channel";
    public static final String URL_VR_PLAY = SERVER + "/channel/get-row-video";
    //    分类列表接口
    public static final String URL_CLASSIFY_LIST = SERVER + "/channel/get-video";
    //    海报打开详情接口
    public static final String URL_DETAIL = SERVER + "/video/get-detail";
    //    播放接口
    public static final String URL_PLAY = SERVER + "/video/play";
    //    关于我们接口
    public static final String URL_ABOUT = SERVER + "/info/about-us";
    //    版本更新接口
//    public static final String URL_UPDATE = SERVER + "/info/update";
//    帮助接口
    public static final String URL_HELP = SERVER + "/info/help";
    //    统计设备接口
    public static final String URL_DEVICE = SERVER + "/count/device";

    //    统计播放接口
    public static final String URL_PLAY_count = SERVER + "/count/player";

//    public static final String URL_HOME = "127.0.0.1/v1.1/";
}