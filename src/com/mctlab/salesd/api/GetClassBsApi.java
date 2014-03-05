package com.mctlab.salesd.api;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.network.api.get.AbsGetJsonArrayApi;
import com.mctlab.salesd.constant.ThisUrl;
import com.mctlab.salesd.data.ClassB;

import org.json.JSONObject;

/**
 * @author liqiang
 */
public class GetClassBsApi extends AbsGetJsonArrayApi<AsEmptyConst.EMPTY_FORM, ClassB> {

    public GetClassBsApi() {
        super(ThisUrl.getClassBsUrl(), AsEmptyConst.EMPTY_FORM_INSTANCE);
    }

    @Override
    protected ClassB decodeJson(JSONObject obj) throws DecodeResponseException {
        return JsonMapper.parseJsonObject(obj, ClassB.class);
    }

    @Override
    protected String apiName() {
        return GetClassBsApi.class.getSimpleName();
    }
}
