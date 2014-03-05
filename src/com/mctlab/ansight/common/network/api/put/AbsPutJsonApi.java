package com.mctlab.ansight.common.network.api.put;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.json.IJsonable;
import com.mctlab.ansight.common.util.HttpUtils;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

public abstract class AbsPutJsonApi<JSON extends IJsonable, RESULT> extends AbsPutApi<AsEmptyConst.EMPTY_FORM, RESULT> {

    private String content;

    public AbsPutJsonApi(String baseUrl, JSON json) {
        super(baseUrl, AsEmptyConst.EMPTY_FORM_INSTANCE);
        content = json.writeJson();
    }

    @Override
    protected void onPreProcess(HttpUriRequest request) {
        super.onPreProcess(request);
        HttpUtils.setEntity((HttpPut) request, content);
    }

}
