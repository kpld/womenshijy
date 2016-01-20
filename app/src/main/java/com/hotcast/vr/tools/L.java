package com.hotcast.vr.tools;


import android.util.Log;

public class L {

    //是否打印日志
    public static boolean mAddLog = false;

    public static void v(String tag, String msg) {
        if (mAddLog) {
            Log.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mAddLog) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mAddLog) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mAddLog) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mAddLog) {
            Log.e(tag, msg);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * TAG
     */
    public static final String DEFAULT_MARKET_TAG = "leyi";

    public static void v(String msg) {
        if (mAddLog == true) {
            Logger.v(getTagName(), msg);
        }
    }

    public static void d(String msg) {
        if (mAddLog == true) {
            Logger.d(getTagName(), msg);
        }
    }

    public static void i(String msg) {
        if (mAddLog == true) {
            Logger.i(getTagName(), msg);
        }
    }

    public static void w(String msg) {
        if (mAddLog == true) {
            Logger.w(getTagName(), msg);
        }
    }

    public static void e(String msg) {
        if (mAddLog == true) {

            Logger.e(getTagName(), msg);
        }
    }

    public static void json(String msg) {
        if (mAddLog == true) {
            Logger.json(msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (mAddLog == true) {
            Log.e(getTagName(), msg, tr);
        }
    }

    private static String getTagName() {
        return DEFAULT_MARKET_TAG;
    }
}
