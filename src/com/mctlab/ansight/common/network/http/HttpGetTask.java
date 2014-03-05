package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpGetTask<Result> extends AbsHttpTask<Result> {

    public HttpGetTask(String baseUrl, IForm form, ExecutorCallback<Result> callback) {
        super(HttpUtils.generateGetUrl(baseUrl, form), form, callback);
    }

    @Override
    protected HttpUriRequest onCreateRequest() {
        return new HttpGet(getUrl());
    }
}
