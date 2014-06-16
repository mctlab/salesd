package com.mctlab.ansight.common.network.api.get;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.JsonException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * for results cannot parse to json, eg. int
 */
public abstract class AbsGetArrayApi<Form extends IForm, Result> extends AbsGetApi<Form, List<Result>> {

    protected AbsGetArrayApi(String baseUrl, Form form) {
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
                    Object obj = jsonArray.get(i);
                    list.add(decode(obj));
                }
            } catch (JSONException e) {
                throw new JsonException(e);
            }
        }
        return list;
    }

    protected abstract Result decode(Object obj) throws DecodeResponseException;
}
