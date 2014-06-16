package com.mctlab.ansight.common.network.api.put;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

public abstract class AbsPutJsonGetJsonApi<JSON extends IJsonable, RESULT extends IJsonable> extends AbsPutJsonApi<JSON, RESULT> {

    public AbsPutJsonGetJsonApi(String baseUrl, JSON json) {
        super(baseUrl, json);
    }

    @Override
    final protected RESULT decodeResponse(HttpResponse response) throws DecodeResponseException {
        return decodeJson(HttpUtils.responseToJson(response));
    }

    protected abstract RESULT decodeJson(JSONObject json) throws DecodeResponseException;
}
