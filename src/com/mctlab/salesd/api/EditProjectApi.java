package com.mctlab.salesd.api;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.network.api.callback.ApiCallbackListener;
import com.mctlab.ansight.common.network.api.post.AbsPostJsonArrayApi;
import com.mctlab.salesd.constant.SalesDConstant;
import com.mctlab.salesd.constant.SalesDUrl;
import com.mctlab.salesd.data.Project;
import com.mctlab.salesd.provider.TasksDatabaseHelper.Tables;
import com.mctlab.salesd.request.Request;
import com.mctlab.salesd.request.RequestDelete;
import com.mctlab.salesd.request.RequestInsert;
import com.mctlab.salesd.request.RequestUpdate;

import org.apache.http.HttpResponse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by liqiang on 6/4/14.
 */
public class EditProjectApi extends AbsPostJsonArrayApi<Request, Void> {

    public static abstract class CallbackListener extends ApiCallbackListener<Void> {}

    public EditProjectApi(List<Request> requests) {
        super(SalesDUrl.getRequestUrl(), requests);
    }

    @Override
    protected Void decodeResponse(HttpResponse response) throws DecodeResponseException {
        return null;
    }

    @Override
    protected String apiName() {
        return EditProjectApi.class.getSimpleName();
    }

    public static void insertProject(int token, Project project, CallbackListener callback) {
        EditProjectApi api = new EditProjectApi(getRequestInsertList(project));
        api.setCallbackListener(token, callback);
        api.call(null);
    }

    public static List<Request> getRequestInsertList(Project project) {
        List<Request> requests = new LinkedList<Request>();
        RequestInsert request = new RequestInsert(Tables.PROJECTS, SalesDConstant.OP_INSERT, project);
        requests.add(request);
        return requests;
    }

    public static void updateProject(int token, Project project, CallbackListener callback) {
        EditProjectApi api = new EditProjectApi(getRequestUpdateList(project));
        api.setCallbackListener(token, callback);
        api.call(null);
    }

    public static List<Request> getRequestUpdateList(Project project) {
        List<Request> requests = new LinkedList<Request>();
        RequestUpdate request = new RequestUpdate(Tables.PROJECTS, SalesDConstant.OP_UPDATE, project);
        requests.add(request);
        return requests;
    }

    public static void deleteProject(int token, int serverId, CallbackListener callback) {
        EditProjectApi api = new EditProjectApi(getRequestDeleteList(serverId));
        api.setCallbackListener(token, callback);
        api.call(null);
    }

    public static List<Request> getRequestDeleteList(int serverId) {
        List<Request> requests = new LinkedList<Request>();
        RequestDelete request = new RequestDelete(Tables.PROJECTS, SalesDConstant.OP_DELETE, serverId);
        requests.add(request);
        return requests;
    }
}
