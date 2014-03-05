package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpPutTask<Result> extends AbsHttpTask<Result> {

    public HttpPutTask(String baseUrl, IForm form, ExecutorCallback<Result> callback) {
        super(baseUrl, form, callback);
    }

    @Override
    protected HttpUriRequest onCreateRequest() {
        return new HttpPut(getUrl());
    }

}