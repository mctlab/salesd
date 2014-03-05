package com.mctlab.ansight.common.network.form;

import com.mctlab.ansight.common.util.UrlUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class BaseForm implements IForm {

    protected List<NameValuePair> params;

    public BaseForm() {
        params = new ArrayList<NameValuePair>();
    }

    public void addParam(String name, double value) {
        params.add(new BasicNameValuePair(name, String.valueOf(value)));
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addParam(String name, long value) {
        addParam(name, String.valueOf(value));
    }

    public void addParam(String name, int value) {
        addParam(name, String.valueOf(value));
    }

    public void addParam(String name, boolean value) {
        addParam(name, String.valueOf(value));
    }

    @Override
    public List<NameValuePair> listParams() {
        return params;
    }

    public String toQueryString() {
        StringBuilder builder = new StringBuilder();
        for (NameValuePair pair : params) {
            String value = UrlUtils.encodeUrl(pair.getValue());
            builder.append(pair.getName()).append('=').append(value).append('&');
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (NameValuePair pair : params) {
            sb.append(pair.getName() + " = " + pair.getValue() + "\n");
        }
        sb.append("]\n");
        return sb.toString();
    }

}