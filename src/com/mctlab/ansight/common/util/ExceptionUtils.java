package com.mctlab.ansight.common.util;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.exception.HttpStatusException;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ExceptionUtils {

    public static int getHttpStatusCode(Throwable error) {
        if (error != null && (error instanceof HttpStatusException)) {
            HttpStatusException e = (HttpStatusException) error;
            return e.getStatusCode();
        } else {
            return -1;
        }
    }

    public static boolean causedByTimeout(Throwable exception) {
        if (exception == null) {
            return false;
        }
        Throwable e = exception;
        while (e != null) {
            if (e instanceof SocketTimeoutException) {
                return true;
            }
            if (e instanceof ConnectTimeoutException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    public static boolean causedByRequestAborted(Throwable exception) {
        if (exception == null || !(exception instanceof IOException)) {
            return false;
        }
        Throwable e = exception;
        while (e != null) {
            if (e instanceof SocketException && e.getMessage() != null && e.getMessage().contains("Socket closed")) {
                return true;
            }
            if (e.getMessage() != null && e.getMessage().contains("Connection already shutdown")) {
                return true;
            }
            if (e.getMessage() != null && e.getMessage().contains("aborted")) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

    public static String dump(Throwable e) {
        if (e == null) {
            return AsEmptyConst.EMPTY_STRING;
        }
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw, true);
        e.printStackTrace(writer);
        return sw.toString();
    }

}
