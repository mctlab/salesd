package com.mctlab.ansight.common.util;

import android.content.Context;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.AsRuntime;
import com.mctlab.ansight.common.DeviceConfig;

public class UIUtils {

    /* toast with context */

    public static void toast(Context context, int msgId) {
        toast(context, msgId, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, int msgId, int duration) {
        Toast.makeText(context, msgId, duration).show();
    }

    public static void toast(Context context, String msg) {
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }

    /* toast without context */

    public static void toast(int stringId, Object... args) {
        AsApplication app = AsApplication.getInstance();
        String str = app.getString(stringId, args);
        toast(str);
    }

    public static void toast(int stringId) {
        toast(stringId, Toast.LENGTH_SHORT);
    }

    public static void toast(String msg) {
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toast(int stringId, int duration) {
        AsApplication app = AsApplication.getInstance();
        toast(app.getString(stringId), duration);
    }

    public static void toast(String msg, int duration) {
        Context context = AsRuntime.getInstance().getCurrentActivity();
        if (AsRuntime.getInstance().isCurrentlyForeground()
                && Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            Toast.makeText(context, msg, duration).show();
        }
    }

    public static void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void invisibleView(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setTextSizeById(TextView textView, int dimenId) {
        setTextSize(textView, textView.getResources().getDimension(dimenId));
    }

    public static void setTextSizeBySp(TextView textView, int spValue) {
        setTextSize(textView, sp2pix(spValue));
    }

    public static void setTextSize(TextView textView, float dimension) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
    }

    public static int dip2pix(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DeviceConfig.getInstance().getDisplayMetrics());
    }

    public static int sp2pix(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, DeviceConfig.getInstance().getDisplayMetrics());
    }

}
