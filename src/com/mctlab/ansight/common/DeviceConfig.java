package com.mctlab.ansight.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;

public class DeviceConfig {

    private DeviceConfig() {}

    private static DeviceConfig me;

    public static DeviceConfig getInstance() {
        if (me == null) {
            synchronized (DeviceConfig.class) {
                if (me == null) {
                    me = new DeviceConfig();
                }
            }
        }
        return me;
    }

    //region device

    /**
     * get the file store path
     * if sd card found, storeDir=/sdcard/Android/data/{package.name}
     * else storeDir is internal flash
     */
    public File getStoreDir() {
        if (hasSdCard()) {
            if (EXTERNAL_DIR == null) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/Android/data/" + AsAppConfig.getInstance().getPackageName();
                EXTERNAL_DIR = new File(path);
            }
            return EXTERNAL_DIR;
        } else {
            return getApp().getApplicationContext().getFilesDir();
        }
    }

    private File EXTERNAL_DIR;

    public boolean hasSdCard() {
        return Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState());
    }

    public boolean hasCamera() {
        return getApp().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public boolean hasCameraAutoFocus() {
        return getApp().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }

    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    private WindowManager getWindowManager() {
        try {
            return AsRuntime.getInstance().getCurrentActivity().getWindowManager();
        } catch (Throwable e) {
            return (WindowManager) getApp().getSystemService(Context.WINDOW_SERVICE);
        }
    }

    //endregion
    //region network

    public boolean isNetworkAvailable() {
        return getNetworkInfo() != null && getNetworkInfo().isConnected();
    }

    public boolean isWifiAvailable() {
        return getNetworkInfo() != null && getNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    private NetworkInfo networkInfo;

    private NetworkInfo getNetworkInfo() {
        if (networkInfo == null) {
            initNetworkInfo();
        }
        return networkInfo;
    }

    private void initNetworkInfo() {
        ConnectivityManager connMgr = (ConnectivityManager) getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
    }

    //endregion
    //region platform

    public int getPlatformCode() {
        return android.os.Build.VERSION.SDK_INT;
    }

    //endregion
    /* get app */

    private AsApplication getApp() {
        return AsApplication.getInstance();
    }

}
