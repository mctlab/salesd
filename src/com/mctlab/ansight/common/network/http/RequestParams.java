package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.util.L;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RequestParams {

    public static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> urlParams;

    /**
     * Constructs a new empty <code>RequestParams</code> instance.
     */
    public RequestParams() {
        init();
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    public HttpEntity getEntity() {
        try {
            return new UrlEncodedFormEntity(getParamsList(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            L.e(this, e);
            return null;
        }
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return params;
    }

}