package com.mctlab.salesd.constant;

/**
 * @author liqiang
 */
public class ThisUrl {

    private static final String PREFIX_FAKE = "ansight://local";

    private static String rootUrl() {
        return PREFIX_FAKE;
    }

    public static String getClassAUrl(int id) {
        return rootUrl() + "/classa/" + id;
    }

    public static String getClassBsUrl() {
        return rootUrl() + "/classb/list";
    }
}
