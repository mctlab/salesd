package com.mctlab.ansight.common.network.api.post;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * method: POST
 * request: JSON
 * response: undefined
 */
public abstract class AbsPostJsonApi<JSON extends IJsonable, RESULT> extends AbsPostApi<AsEmptyConst.EMPTY_FORM, RESULT> {

    private String content;

    public AbsPostJsonApi(String baseUrl, JSON json) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = json.writeJson();
    }

    @Override
    protected void onPreProcess(HttpUriRequest request) {
        super.onPreProcess(request);
        HttpUtils.setEntity((HttpPost) request, content);
    }

}
