package com.mctlab.ansight.common;

import android.app.Application;

import com.mctlab.ansight.common.misc.CrashHandler;

public abstract class AsApplication extends Application {

    private static AsApplication me;

    public static AsApplication getInstance() {
        return me;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init();
        me = this;
    }

    public abstract void initAppConfig();

    public abstract void initRuntime();

}
