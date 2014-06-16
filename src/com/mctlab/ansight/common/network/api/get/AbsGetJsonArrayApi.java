package com.mctlab.ansight.common.network.api.get;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.JsonException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsGetJsonArrayApi<Form extends IForm, Result> extends AbsGetApi<Form, List<Result>> {

    protected AbsGetJsonArrayApi(String baseUrl, Form form) {
        super(baseUrl, form);
    }

    @Override
    protected final List<Result> decodeResponse(HttpResponse response) throws DecodeResponseException {
        JSONArray jsonArray = HttpUtils.responseToJsonArray(response);
        List<Result> list = new ArrayList<Result>();
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

    protected abstract Result decodeJson(JSONObject obj) throws DecodeResponseException;
}
