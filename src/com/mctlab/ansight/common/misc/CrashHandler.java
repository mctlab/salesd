package com.mctlab.ansight.common.misc;

import android.os.Looper;
import android.widget.Toast;

import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.AsRuntime;
import com.mctlab.ansight.common.util.L;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler me;

    private CrashHandler() {}

    public static CrashHandler getInstance() {
        if (me != null) {
            return me;
        }
        synchronized (CrashHandler.class) {
            if (me == null) {
                me = new CrashHandler();
            }
        }
        return me;
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        L.e(this, ex);
        final AsApplication app = AsApplication.getInstance();
        if (thread.getId() == Looper.getMainLooper().getThread().getId()) {
            if (AsRuntime.getInstance().isCurrentlyForeground()) {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(app, "抱歉，程序异常，即将退出", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }.start();
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {}
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
