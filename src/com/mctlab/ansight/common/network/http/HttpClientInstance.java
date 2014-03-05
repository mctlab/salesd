package com.mctlab.ansight.common.network.http;

import android.net.http.AndroidHttpClient;

public class HttpClientInstance {

    private static final String USER_AGENT = "ansight";

    public static AndroidHttpClient newInstance() {
        return AndroidHttpClient.newInstance(USER_AGENT);
    }

}
