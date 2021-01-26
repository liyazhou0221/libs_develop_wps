package cn.wps.moffice.demo.util;

import android.util.Log;

import cn.wps.moffice.demo.BuildConfig;

public class WpsLogger {
    // 是否显示WPS插件日志
    public static final boolean showWpsLogger = true;

    public static void e(String TAG, String msg) {
        if (showWpsLogger && BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
