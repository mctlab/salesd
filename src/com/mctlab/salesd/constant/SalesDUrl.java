package com.mctlab.salesd.constant;

/**
 * Created by liqiang on 6/4/14.
 */
public class SalesDUrl {

    private static final String PREFIX = "http://salesdbackend.duapp.com";

    private static String rootUrl() {
        return PREFIX;
    }

    private static String dataUrl() {
        return rootUrl() + "/data";
    }

    public static String getSyncUrl() {
        return dataUrl() + "/sync";
    }

    public static String getRequestUrl() {
        return dataUrl() + "/request";
    }
}
