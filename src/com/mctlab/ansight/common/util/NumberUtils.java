package com.mctlab.ansight.common.util;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Number utils for converting one format to another.
 */
public class NumberUtils {

    public static long toLong(String str) {
        return toLong(str, 0);
    }

    public static long toLong(String str, int defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0f);
    }

    public static float toFloat(String str, float defaultValue) {
        if (StringUtils.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String formatFloat(float f, int precision) {
        MathContext mathContext = new MathContext(precision);
        BigDecimal decimal = new BigDecimal(f);
        return decimal.round(mathContext).toString();
    }

    public static int hash(int i, int j) {
        return i * 31 + j;
    }

}
