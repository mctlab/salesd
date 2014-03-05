package com.mctlab.ansight.common.util;

import android.util.Log;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.exception.NetworkNotAvailableException;

public class L {

    public static void e(Object context, String msg, Object... args) {
        e(context, String.format(msg, args));
    }

    public static void e(Object context, String msg) {
        Log.e(contextName(context), msg);
    }

    public static void e(Object context, String msg, Throwable e) {
        if (e != null) {
            if (e instanceof NetworkNotAvailableException) {
                w(context, msg, e);
                return;
            }
            Log.e(contextName(context), msg, e);
        } else {
            e(context, msg);
        }
    }

    public static void e(Object context, Throwable e) {
        if (e != null) {
            if (e instanceof NetworkNotAvailableException) {
                w(context, e);
                return;
            }
            Log.e(contextName(context), "", e);
        }
    }

    public static void i(Object context, String msg) {
        i(context, msg, null);
    }

    public static void i(Object context, Throwable e) {
        i(context, AsEmptyConst.EMPTY_STRING, e);
    }

    public static void i(Object context, String msg, Throwable e) {
        if (e == null) {
            Log.i(contextName(context), msg);
        } else {
            Log.i(contextName(context), msg, e);
        }
    }

    public static void d(Object context, String msg) {
        Log.d(contextName(context), msg);
    }

    public static void d(Object context, Throwable e) {
        Log.d(contextName(context), "", e);
    }

    public static void w(Object context, String msg) {
        Log.w(contextName(context), msg);
    }

    public static void w(Object context, Throwable e) {
        Log.w(contextName(context), "", e);
    }

    public static void w(Object context, String msg, Throwable e) {
        Log.w(contextName(context), msg, e);
    }

    private static String contextName(Object context) {
        String contextName = (context instanceof String) ? (String) context : context.getClass().getSimpleName();
        return "ansight-log-" + contextName;
    }

}
