package com.mctlab.ansight.common.util;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.network.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

    public static String responseToString(HttpResponse response) throws DecodeResponseException {
        InputStream is = null;
        try {
            is = responseToStream(response);
            String str = IOUtils.toString(is);
            L.i("HttpUtils", "response string : " + str);
            return str;
        } catch (Exception e) {
            throw new DecodeResponseException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static JSONObject responseToJson(HttpResponse response) throws DecodeResponseException {
        String json = responseToString(response);
        JSONObject obj;
        try {
            obj = new JSONObject(json);
            return obj;
        } catch (JSONException e) {
            throw new DecodeResponseException(e);
        }
    }

    public static JSONArray responseToJsonArray(HttpResponse response) throws DecodeResponseException {
        String str = responseToString(response);
        return stringToJsonArray(str);
    }

    public static JSONArray stringToJsonArray(String str) throws DecodeResponseException {
        try {
            Object o = new JSONTokener(str).nextValue();
            if (o == null || !(o instanceof JSONArray)) {
                throw new DecodeResponseException("parse json array failed");
            }
            return (JSONArray) o;
        } catch (JSONException e) {
            throw new DecodeResponseException(e);
        }
    }

    public static InputStream responseToStream(HttpResponse response) throws ApiException {
        try {
            InputStream instream = response.getEntity().getContent();
            Header contentEncoding = response.getFirstHeader("Content-Encoding");
            if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                instream = new GZIPInputStream(instream);
            }
            return instream;
        } catch (IllegalStateException e) {
            throw new ApiException(e);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    public static int getStatusCode(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    public static int getContentSize(HttpResponse response) {
        Header header = response.getFirstHeader("Content-Length");
        if (header == null) {
            return 0;
        } else {
            return NumberUtils.toInt(header.getValue());
        }
    }

    public static String getCookie(HttpResponse response, String name) {
        Header[] headers = response.getHeaders("Set-Cookie");
        for (Header header : headers) {
            String[] values = header.getValue().split(";");
            for (String value : values) {
                if (value.startsWith(name)) {
                    int idxEq = header.getValue().indexOf("=");
                    return value.substring(idxEq + 1);
                }
            }
        }
        return "";
    }

    public static NameValuePair[] getCookies(HttpResponse response) {
        List<NameValuePair> cookies = new ArrayList<NameValuePair>();
        Header[] headers = response.getHeaders("Set-Cookie");
        for (Header header : headers) {
            String[] values = header.getValue().split(";");
            for (String value : values) {
                int idxEq = value.indexOf("=");
                if (idxEq > 0 && idxEq < header.getValue().length() - 1) {
                    String name = value.substring(0, idxEq);
                    String v = value.substring(idxEq + 1);
                    NameValuePair cookie = new BasicNameValuePair(name, v);
                    cookies.add(cookie);
                }
            }
        }
        return cookies.toArray(new BasicNameValuePair[0]);
    }

    public static void setCookies(HttpUriRequest request) {
        // TODO: setCookies
    }

    public static void saveCookies(HttpResponse response) {
        // TODO: saveCookies
    }

    /* for get */

    public static String generateHeadUrl(String baseUrl, IForm form) {
        return generateUrlWithParams(baseUrl, form);
    }

    public static String generateGetUrl(String baseUrl, IForm form) {
        return generateUrlWithParams(baseUrl, form);
    }

    private static String generateUrlWithParams(String baseUrl, IForm form) {
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        if (form != null) {
            boolean isFirst = true;
            for (NameValuePair pair : form.listParams()) {
                if (isFirst) {
                    sb.append("?");
                    isFirst = false;
                } else {
                    sb.append("&");
                }
                sb.append(pair.getName());
                sb.append("=");
                sb.append(UrlUtils.encodeUrl(pair.getValue()));
            }
        }
        return sb.toString();
    }

    /* for post */

    public static RequestParams generatePostParams(IForm form) {
        RequestParams params = new RequestParams();
        if (form != null) {
            for (NameValuePair e : form.listParams()) {
                params.put(e.getName(), e.getValue());
            }
        }
        return params;
    }

    public static void setEntity(HttpEntityEnclosingRequestBase request, String content) {
        try {
            request.setEntity(new StringEntity(content, "UTF-8"));
            L.i("HttpUtils", "post json = " + content);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        request.addHeader("Content-Type", "application/json;charset=UTF-8");
    }

    /* for cache */

    public static String getHeaderString(String key, HttpResponse response) {
        Header[] headers = response.getHeaders(key);
        return headers != null && headers.length > 0 ? headers[0].getValue().trim() : "";
    }
}
