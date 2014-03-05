package com.mctlab.ansight.common.util;

public class UnitUtils {

    public static final long SECOND = 1000;

    public static final long MINUTE = 60 * SECOND;

    public static final long HOUR = 60 * MINUTE;

    public static final long DAY = 24 * HOUR;

    public static final long WEEK = 7 * DAY;

    public static final long BYTE = 1;

    public static final long KBYTE = 1024 * BYTE;

    public static final long MBYTE = 1024 * KBYTE;

    public static final long GBYTE = 1024 * MBYTE;

    public static int getDays(long time) {
        return Math.round(((float) time / DAY));
    }

    public static int getHours(long time) {
        return Math.round(((float) time / HOUR));
    }

    public static int getMinutes(long time) {
        return Math.round(((float) time / MINUTE));
    }

    public static int getSeconds(long time) {
        return Math.round(((float) time / SECOND));
    }

    public static String getDuration(long timeInMs) {
        int h = (int) (timeInMs / UnitUtils.HOUR);
        int m = (int) ((timeInMs % UnitUtils.HOUR) / UnitUtils.MINUTE);
        int s = (int) ((timeInMs % UnitUtils.MINUTE) / UnitUtils.SECOND);
        if (h == 0) {
            return toStr(m) + ":" + toStr(s);
        } else {
            return toStr(h) + ":" + toStr(m) + ":" + toStr(s);
        }
    }

    private static String toStr(int x) {
        if (x / 10 == 0) {
            return "0" + x;
        } else {
            return Integer.toString(x);
        }
    }

}
