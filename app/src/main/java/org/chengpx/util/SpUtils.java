package org.chengpx.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * create at 2018/5/10 15:44 by chengpx
 */
public class SpUtils {


    private static SpUtils sSpUtils;
    private final SharedPreferences mConfig;

    private SpUtils(Context context) {
        mConfig = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public static SpUtils getInstance(Context context) {
        if (sSpUtils == null) {
            synchronized (SpUtils.class) {
                if (sSpUtils == null) {
                    sSpUtils = new SpUtils(context);
                }
            }
        }
        return sSpUtils;
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return mConfig.getBoolean(key, defaultVal);
    }

    public int getInt(String key, int defaultVal) {
        return mConfig.getInt(key, defaultVal);
    }

    public void putInt(String key, int val) {
        SharedPreferences.Editor edit = mConfig.edit();
        edit.putInt(key, val).apply();
    }

    public void putBoolean(String key, boolean val) {
        SharedPreferences.Editor edit = mConfig.edit();
        edit.putBoolean(key, val).apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = mConfig.edit();
        editor.remove(key).apply();
    }
}
