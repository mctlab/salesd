package com.mctlab.salesd.api;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.RequestAbortedException;
import com.mctlab.ansight.common.json.JsonMapper;
import com.mctlab.ansight.common.network.api.callback.ApiCallbackListener;
import com.mctlab.ansight.common.network.api.post.AbsPostJsonArrayGetJsonArrayApi;
import com.mctlab.salesd.constant.SalesDUrl;
import com.mctlab.salesd.data.Sync;
import com.mctlab.salesd.data.SyncData;
import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

/**
 * Created by liqiang on 6/16/14.
 */
public class SyncApi extends AbsPostJsonArrayGetJsonArrayApi<Sync, SyncData> {

    public static abstract class CallbackListener extends ApiCallbackListener<List<SyncData>> {

        @Override
        public void onApiStart(int token) {
            super.onApiStart(token);
        }

        @Override
        public void onApiAborted(int token, RequestAbortedException exception) {
            super.onApiAborted(token, exception);
        }

        @Override
        public void onApiSuccess(int token, List<SyncData> syncDatas) {
            super.onApiSuccess(token, syncDatas);
        }

        @Override
        public void onApiFailed(int token, ApiException exception) {
            super.onApiFailed(token, exception);
        }

        @Override
        public void onApiFinish(int token) {
            super.onApiFinish(token);
        }

    };

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

    public static SyncApi syncProjects(int token, long version, CallbackListener callback) {
        List<Sync> syncs = new LinkedList<Sync>();
        Sync syncProject = new Sync(Tables.PROJECTS, version);
        syncs.add(syncProject);

        SyncApi syncApi = new SyncApi(syncs.toArray(new Sync[syncs.size()]));
        syncApi.setCallbackListener(token, callback);
        syncApi.call(null);
        return syncApi;
    }
}
