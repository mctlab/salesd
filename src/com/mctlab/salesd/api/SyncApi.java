package com.mctlab.salesd.api;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.network.api.post.AbsPostJsonArrayGetJsonArrayApi;
import com.mctlab.salesd.constant.SalesDUrl;
import com.mctlab.salesd.data.Sync;
import com.mctlab.salesd.data.SyncData;

import org.json.JSONObject;

/**
 * Created by liqiang on 6/16/14.
 */
public class SyncApi extends AbsPostJsonArrayGetJsonArrayApi<Sync, SyncData> {

    public SyncApi(Sync[] array) {
        super(SalesDUrl.getSyncUrl(), array);
    }

    @Override
    protected SyncData decodeJson(JSONObject obj) throws DecodeResponseException {
        return JsonMapper.parseJsonObject(obj, SyncData.class);
    }

    @Override
    protected String apiName() {
        return SyncApi.class.getSimpleName();
    }
}
