package com.mctlab.ansight.common.exception;

import com.mctlab.ansight.common.util.HttpUtils;
import com.mctlab.ansight.common.util.L;

import org.apache.http.HttpResponse;

public class HttpStatusException extends ApiException {

    private final int statusCode;
    private final HttpResponse response;

    public HttpStatusException(int statusCode) {
        this(statusCode, null);
    }

    public HttpStatusException(int statusCode, HttpResponse response) {
        super("statusCode=" + statusCode);
        this.statusCode = statusCode;
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public String getResponseString() {
        if (response == null) {
            return "";
        }
        try {
            return HttpUtils.responseToString(response);
        } catch (DecodeResponseException e) {
            L.e(this, e);
            return "";
        }
    }

}
