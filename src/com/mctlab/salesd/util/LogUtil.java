package com.mctlab.salesd.util;

import android.util.Log;

public class LogUtil {
    public static final String TAG = "SalesD";

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void d(String message, Throwable tr) {
        Log.d(TAG, message, tr);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void w(String message) {
        Log.w(TAG, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }
}
