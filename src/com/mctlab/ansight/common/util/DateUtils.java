package com.mctlab.ansight.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private final static SimpleDateFormat SHORT_FORMATTER = new SimpleDateFormat("HH:mm");

    private final static SimpleDateFormat CN_FORMATTER = new SimpleDateFormat("yyyy年MM月dd日");

    private final static SimpleDateFormat CN_TIME_FORMATTER = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public static String chineseDate(long time) {
        return CN_FORMATTER.format(new Date(time));
    }

    public static String chineseTime(long time) {
        return CN_TIME_FORMATTER.format(new Date(time));
    }

    public static String prettyTime(long time) {
        long current = System.currentTimeMillis();
        long diff = current - time;
        if (diff < 0) {
            // time is the future
            return FORMATTER.format(new Date(time));
        }
        // just a few seconds
        if (diff < UnitUtils.MINUTE) {
            return diff / UnitUtils.SECOND + "秒前";
        }
        // a few minutes
        if (diff < UnitUtils.HOUR) {
            return diff / UnitUtils.MINUTE + "分钟前";
        }
        //a few hours
        if (isToday(time)) {
            return "今天 " + SHORT_FORMATTER.format(new Date(time));
        } else if (isYesterday(time)) {
            return "昨天 " + SHORT_FORMATTER.format(new Date(time));
        } else {
            return FORMATTER.format(new Date(time));
        }
    }

    public static String shortPrettyTime(long time) {
        long current = System.currentTimeMillis();
        long diff = current - time;
        if (diff < 0) {
            // time is the future
            return FORMATTER.format(new Date(time));
        }
        // just a few seconds
        if (diff < UnitUtils.MINUTE) {
            return "刚刚";
        }
        // a few minutes
        if (diff < UnitUtils.HOUR) {
            return diff / UnitUtils.MINUTE + "分钟前";
        }
        //a few hours
        if (isToday(time)) {
            return "今天 " + SHORT_FORMATTER.format(new Date(time));
        } else if (isYesterday(time)) {
            return "昨天 " + SHORT_FORMATTER.format(new Date(time));
        } else {
            return FORMATTER.format(new Date(time));
        }
    }

    /**
     * Is the given time today?
     */
    public static boolean isToday(long time) {
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();
        return diff(that, now, Calendar.YEAR) == 0 &&
                diff(that, now, Calendar.MONTH) == 0 &&
                diff(that, now, Calendar.DATE) == 0;
    }

    /**
     * Is the given time yesterday?
     */
    public static boolean isYesterday(long time) {
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();
        return diff(that, now, Calendar.YEAR) == 0 &&
                diff(that, now, Calendar.MONTH) == 0 &&
                diff(that, now, Calendar.DATE) == -1;
    }

    public static boolean isThisYear(long time) {
        Calendar that = Calendar.getInstance();
        that.setTimeInMillis(time);
        Calendar now = Calendar.getInstance();
        return diff(that, now, Calendar.YEAR) == 0;
    }

    private static int diff(Calendar thiz, Calendar that, int type) {
        return thiz.get(type) - that.get(type);
    }

}
