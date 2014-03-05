package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpPostTask<Result> extends AbsHttpTask<Result> {

    public HttpPostTask(String baseUrl, IForm form, ExecutorCallback<Result> callback) {
        super(baseUrl, form, callback);
    }

    @Override
    protected HttpUriRequest onCreateRequest() {
        RequestParams params = HttpUtils.generatePostParams(form);
        HttpPost request = new HttpPost(getUrl());
        request.setEntity(params.getEntity());
        return request;
    }

}
