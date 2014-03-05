package com.mctlab.ansight.common;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mctlab.ansight.common.util.L;

public abstract class AsAppConfig {

    protected AsAppConfig() {}

    protected static AsAppConfig me;

    public static AsAppConfig getInstance() {
        if (me == null) {
            synchronized (AsAppConfig.class) {
                if (me == null) {
                    AsApplication.getInstance().initAppConfig();
                }
            }
        }
        return me;
    }

    public boolean isDebug() {
        return true;
    }

    private PackageInfo packageInfo;

    public PackageInfo getPackageInfo() {
        if (packageInfo == null) {
            try {
                packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (NameNotFoundException e) {
                L.e(this, e);
            }
        }
        return packageInfo;
    }

    private PackageManager getPackageManager() {
        return getApp().getPackageManager();
    }

    public String getPackageName() {
        return getApp().getPackageName();
    }

    /* version */

    public String getVersionName() {
        return getPackageInfo().versionName;
    }

    public int getVersionCode() {
        return getPackageInfo().versionCode;
    }

    /* get app */

    private AsApplication getApp() {
        return AsApplication.getInstance();
    }

}
