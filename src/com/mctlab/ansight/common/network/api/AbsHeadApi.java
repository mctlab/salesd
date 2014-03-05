package com.mctlab.ansight.common.network.api;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.network.http.HttpHeadTask;

import org.apache.http.HttpResponse;

public abstract class AbsHeadApi<Form extends IForm> extends AbstractApi<Form, Void> {

    protected AbsHeadApi(String baseUrl, Form form) {
        super(baseUrl, form);
    }

    @Override
    protected Void decodeResponse(HttpResponse response) throws DecodeResponseException {
        return null;
    }

    @Override
    protected HttpHeadTask onCreateTask() {
        return newHttpHeadTask();
    }

}
