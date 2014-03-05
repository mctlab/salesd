package com.mctlab.ansight.common.network.api.post;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 * method: POST
 * request: JSON
 * response: JSON
 */
public abstract class AbsPostJsonGetJsonApi<JSON extends IJsonable, RESULT extends IJsonable>
        extends AbsPostJsonApi<JSON, RESULT> {

    public AbsPostJsonGetJsonApi(String baseUrl, JSON json) {
        super(baseUrl, json);
    }

    @Override
    final protected RESULT decodeResponse(HttpResponse response) throws DecodeResponseException {
        return decodeJson(HttpUtils.responseToJson(response));
    }

    protected abstract RESULT decodeJson(JSONObject json) throws DecodeResponseException;

}
