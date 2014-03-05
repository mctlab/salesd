package com.mctlab.ansight.common;

import android.os.Handler;
import android.os.Looper;

import com.mctlab.ansight.common.activity.AsActivity;
import com.mctlab.ansight.common.exception.HttpStatusException;
import com.mctlab.ansight.common.util.UIUtils;

public abstract class AsRuntime {

    protected AsRuntime() {}

    protected static AsRuntime me;

    public static AsRuntime getInstance() {
        if (me == null) {
            synchronized (AsRuntime.class) {
                if (me == null) {
                    AsApplication.getInstance().initRuntime();
                }
            }
        }
        return me;
    }

    private AsActivity currentActivity;
    private Handler uiHandler;

    public void setCurrentActivity(AsActivity activity) {
        this.currentActivity = activity;
        if (activity != null) {
            uiHandler = new Handler();
        } else {
            uiHandler = null;
        }
    }

    public AsActivity getCurrentActivity() {
        return currentActivity;
    }

    public boolean isCurrentlyForeground() {
        if (currentActivity != null && !currentActivity.isPaused()) {
            return true;
        }
        return false;
    }

    /**
     * post runnable to run on UI thread
     */
    public boolean postRunnable(Runnable runnable) {
        if (uiHandler != null) {
            uiHandler.post(runnable);
            return true;
        }
        return false;
    }

    public boolean postRunnableDelayed(Runnable runnable, long delay) {
        if (uiHandler != null) {
            uiHandler.postDelayed(runnable, delay);
            return true;
        }
        return false;
    }

    //region network

    public abstract void onNetworkNotAvailable();

    public abstract void onRequestTimeout();

    public abstract void onHttpStatusCodeError(HttpStatusException error);

    //endregion
    //region toast

    private long lastTimeToast;
    private static final long TOAST_INTERVAL = 2000L;

    public void toast(String msg) {
        if (isCurrentlyForeground()
                && Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            long now = System.currentTimeMillis();
            if (now - lastTimeToast > TOAST_INTERVAL) {
                UIUtils.toast(msg);
                lastTimeToast = now;
            }
        }
    }

    public void toast(int resourceId) {
        toast(getString(resourceId));
    }

    public String getString(int resId) {
        return getApp().getString(resId);
    }

    //endregion
    /* get app */

    private AsApplication getApp() {
        return AsApplication.getInstance();
    }

}
