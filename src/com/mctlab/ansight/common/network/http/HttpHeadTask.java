package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpHeadTask extends AbsHttpTask<Void> {

    public HttpHeadTask(String baseUrl, IForm form, ExecutorCallback<Void> callback) {
        super(HttpUtils.generateHeadUrl(baseUrl, form), form, callback);
    }

    @Override
    protected HttpUriRequest onCreateRequest() {
        return new HttpHead(getUrl());
    }

}
