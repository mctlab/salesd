package com.mctlab.ansight.common.network.api.post;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.constant.AsEmptyConst.EMPTY_FORM;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.JsonException;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * method: POST
 * request: JSON
 * response: undefined
 */
public abstract class AbsPostJsonArrayGetJsonArrayApi<JSON extends IJsonable, RESULT extends IJsonable> extends AbsPostApi<EMPTY_FORM, List<RESULT>> {

    private String content;

    public AbsPostJsonArrayGetJsonArrayApi(String baseUrl, JSON[] array) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = JsonMapper.arrayToJson(array);
    }

    public AbsPostJsonArrayGetJsonArrayApi(String baseUrl, List<JSON> list) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = JsonMapper.listToJson(list);
    }

    @Override
    protected void onPreProcess(HttpUriRequest request) {
        super.onPreProcess(request);
        HttpUtils.setEntity((HttpPost) request, content);
    }

    @Override
    protected final List<RESULT> decodeResponse(HttpResponse response) throws DecodeResponseException {
        JSONArray jsonArray = HttpUtils.responseToJsonArray(response);
        List<RESULT> list = new LinkedList<RESULT>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String json = jsonArray.getString(i);
                if ("null".equals(json)) {
                    list.add(null);
                } else {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    list.add(decodeJson(jsonObj));
                }
            } catch (JSONException e) {
                throw new JsonException(e);
            }
        }
        return list;
    }

    protected abstract RESULT decodeJson(JSONObject obj) throws DecodeResponseException;
}
