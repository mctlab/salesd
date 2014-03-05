package com.mctlab.ansight.common.network.api.get;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public abstract class AbsGetJsonApi<Form extends IForm, Result> extends AbsGetApi<Form, Result> {

    protected AbsGetJsonApi(String baseUrl, Form form) {
        super(baseUrl, form);
    }

    @Override
    protected final Result decodeResponse(HttpResponse response) throws DecodeResponseException {
        return decodeJson(HttpUtils.responseToJson(response));
    }

    protected abstract Result decodeJson(JSONObject obj) throws DecodeResponseException;

}
