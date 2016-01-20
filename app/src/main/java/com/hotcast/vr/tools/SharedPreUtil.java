package com.hotcast.vr.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * lihao
 *
 * @version 1.0
 *          SharedPreferences 工具类
 */
public class SharedPreUtil {

    private static final String SHAREDNAME = "leyi_config";

    private static SharedPreUtil mSharedPreUtil = null;
    private static SharedPreferences mSharedPreference = null;

    private static Context mContext = null;

    private SharedPreUtil(Context context) {
        mSharedPreference = context.getSharedPreferences(SHAREDNAME,
                Context.MODE_PRIVATE);
    }

    public static SharedPreUtil getInstance(Context context) {
        if (mSharedPreUtil == null || mSharedPreference == null) {
            mContext = context;
            mSharedPreUtil = new SharedPreUtil(mContext);
        }
        return mSharedPreUtil;

    }

    public void add(String key, Object value) {
        Editor edit = mSharedPreference.edit();
        if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        }
        edit.commit();
    }

    public void delete(String key) {
        if (mSharedPreference.contains(key)) {
            Editor edit = mSharedPreference.edit();
            edit.remove(key);
            edit.commit();
        }
    }
    public static boolean getBooleanData(Context context,String key, boolean defValue) {
        if (mSharedPreference == null) {
            mSharedPreference = (SharedPreferences) SharedPreUtil.getInstance(context);
        }
        return mSharedPreference.getBoolean(key, defValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T select(String key, T defValue) {
//        if (!mSharedPreference.contains(key)) {
//            return null;
//        }
        Object value = defValue;
        if (defValue instanceof Integer) {
            value = mSharedPreference.getInt(key, (Integer) defValue);
        } else if (defValue instanceof String) {
            value = mSharedPreference.getString(key, (String) defValue);
        } else if (defValue instanceof Boolean) {
            value = mSharedPreference.getBoolean(key, (Boolean) defValue);
        } else if (defValue instanceof Float) {
            value = mSharedPreference.getFloat(key, (Float) defValue);
        } else if (defValue instanceof Long) {
            value = mSharedPreference.getLong(key, (Long) defValue);
        }
        return ((T) value);
    }

    public void clear() {
        Editor edit = mSharedPreference.edit();
        edit.clear();
        edit.commit();
    }
}
