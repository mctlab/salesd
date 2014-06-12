package com.mctlab.ansight.common.network.api.post;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.List;

/**
 * method: POST
 * request: JSON
 * response: undefined
 */
public abstract class AbsPostJsonArrayApi<JSON extends IJsonable, RESULT> extends AbsPostApi<AsEmptyConst.EMPTY_FORM, RESULT> {

    private String content;

    public AbsPostJsonArrayApi(String baseUrl, JSON[] array) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = JsonMapper.arrayToJson(array);
    }

    public AbsPostJsonArrayApi(String baseUrl, List<JSON> list) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = JsonMapper.listToJson(list);
    }

    @Override
    protected void onPreProcess(HttpUriRequest request) {
        super.onPreProcess(request);
        HttpUtils.setEntity((HttpPost) request, content);
    }
}
