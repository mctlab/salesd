package com.mctlab.ansight.common.util;

import com.mctlab.ansight.common.network.form.BaseForm;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UrlUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String HTTP_PROTOCOL = "http://";
    public static final String HTTPS_PROTOCOL = "https://";

    /**
     * Encode URL using defaultEncoding
     *
     * @param s url to be encoded
     * @return url encoded
     */
    public static String encodeUrl(String s) {
        return encodeUrl(s, DEFAULT_ENCODING);
    }

    /**
     * Encode URL using given charset
     *
     * @param s       url to be encoded
     * @param charset UTF-8 or GBK, etc
     * @return the encoded URL in the format "%xx%xx..". If failed, return ""
     */
    public static String encodeUrl(String s, String charset) {
        try {
            return URLEncoder.encode(s, charset);
        } catch (Exception e) {
            return "";
        }
    }

    public static String decodeUrl(String s) {
        return decodeUrl(s, DEFAULT_ENCODING);
    }

    public static String decodeUrl(String s, String encoding) {
        try {
            return URLDecoder.decode(s, encoding);
        } catch (Exception e) {
            return "";
        }
    }

    public static String buildUrl(String url, BaseForm form) {
        StringBuilder builder = new StringBuilder(url);
        if (!url.endsWith("?")) {
            builder.append('&');
        }
        builder.append(form.toQueryString());
        return builder.toString();
    }

    public static String buildUrl(String host, String action, List<NameValuePair> params) {
        return String.format("%s/%s?%s", host, action, pairsToQuery(params));
    }

    /**
     * add or replace a new "name=value" pair to the query part of the originUrl, return the new url
     */
    public static String replaceParam(String originUrl, String name, String value) {
        return replaceParam(originUrl, new String[]{name}, new String[]{value});
    }

    public static String replaceParam(String originUrl, NameValuePair param) {
        return replaceParam(originUrl, param.getName(), param.getValue());
    }

    public static String replaceParam(String originUrl, List<NameValuePair> params) {
        return replaceParam(originUrl, params.toArray(new NameValuePair[0]));
    }

    public static String replaceParam(String originUrl, NameValuePair[] params) {
        if (CollectionUtils.isEmpty(params)) {
            return originUrl;
        }
        String[] names = new String[params.length];
        String[] values = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            names[i] = params[i].getName();
            values[i] = params[i].getValue();
        }
        return replaceParam(originUrl, names, values);
    }

    public static String replaceParam(String originUrl, BaseForm form) {
        return replaceParam(originUrl, form.listParams());
    }

    public static String replaceParam(String originUrl, String[] names, String[] values) {
        if (CollectionUtils.isEmpty(names) || CollectionUtils.isEmpty(values) ||
                names.length != values.length) {
            throw new IllegalArgumentException("names and values doesn't match");
        }

        String[] baseAndQuery = getBaseAndQuery(originUrl);
        String base = baseAndQuery[0];
        String query = baseAndQuery[1];
        List<NameValuePair> pairs = queryToPairs(query);

        Iterator<NameValuePair> it = pairs.iterator();
        while (it.hasNext()) {
            NameValuePair pair = it.next();
            for (String name : names) {
                if (pair.getName().equals(name)) {
                    it.remove();
                    break;
                }
            }
        }

        for (int i = 0; i < names.length; i++) {
            NameValuePair pair = new BasicNameValuePair(names[i], values[i]);
            pairs.add(pair);
        }
        query = pairsToQuery(pairs);

        return mergeBaseAndQuery(base, query);
    }

    public static String mergeBaseAndQuery(String base, String query) {
        if (StringUtils.isBlank(query)) {
            return base;
        }
        return base + "?" + query;
    }

    public static String[] getBaseAndQuery(String url) {
        if (url.contains("?")) {
            return url.split("\\?");
        } else {
            return new String[]{
                    url, ""
            };
        }
    }

    public static String getQuery(String url) {
        if (url.contains("?")) {
            String[] ss = url.split("\\?");
            return ss[1].trim();
        }
        return "";
    }

    public static List<NameValuePair> queryToPairs(String query) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (!StringUtils.isBlank(query)) {
            String[] ss = query.split("&");
            for (String s : ss) {
                String[] strPair = s.split("=");
                String key = strPair[0].trim();
                String value = strPair[1].trim();
                list.add(new BasicNameValuePair(key, value));
            }
        }
        return list;
    }

    public static String pairsToQuery(List<NameValuePair> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : list) {
            if (!first) {
                sb.append("&");
            }
            first = false;
            sb.append(pair.getName()).append("=").append(pair.getValue());
        }
        return sb.toString();
    }

    public static boolean isAbsolutePath(String url) {
        return url.startsWith(HTTP_PROTOCOL) || url.startsWith(HTTPS_PROTOCOL);
    }

}
