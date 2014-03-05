package com.mctlab.ansight.common.network.api.post;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 * method: POST
 * request: FORM
 * response: JSON
 */
public abstract class AbsPostGetJsonApi<FORM extends IForm, RESULT extends IJsonable> extends AbsPostApi<FORM, RESULT> {

    protected AbsPostGetJsonApi(String baseUrl, FORM form) {
        super(baseUrl, form);
    }

    @Override
    protected final RESULT decodeResponse(HttpResponse response) throws DecodeResponseException {
        return decodeJson(HttpUtils.responseToJson(response));
    }

    protected abstract RESULT decodeJson(JSONObject obj) throws DecodeResponseException;

}
