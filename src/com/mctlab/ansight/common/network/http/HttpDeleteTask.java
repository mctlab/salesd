package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpDeleteTask<Result> extends AbsHttpTask<Result> {

    public HttpDeleteTask(String baseUrl, IForm form, ExecutorCallback<Result> callback) {
        super(baseUrl, form, callback);
    }

    @Override
    protected HttpUriRequest onCreateRequest() {
        return new HttpDelete(getUrl());
    }

}
