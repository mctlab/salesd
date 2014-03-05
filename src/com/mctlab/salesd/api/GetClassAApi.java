package com.mctlab.salesd.api;

import com.mctlab.ansight.common.constant.AsEmptyConst;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.network.api.get.AbsGetJsonApi;
import com.mctlab.salesd.constant.ThisUrl;
import com.mctlab.salesd.data.ClassA;

import org.json.JSONObject;

/**
 * @author liqiang
 */
public class GetClassAApi extends AbsGetJsonApi<AsEmptyConst.EMPTY_FORM, ClassA> {

    public GetClassAApi(int id) {
        super(ThisUrl.getClassAUrl(id), AsEmptyConst.EMPTY_FORM_INSTANCE);
    }

    @Override
    protected ClassA decodeJson(JSONObject obj) throws DecodeResponseException {
        return JsonMapper.parseJsonObject(obj, ClassA.class);
    }

    @Override
    protected String apiName() {
        return GetClassAApi.class.getSimpleName();
    }
}
