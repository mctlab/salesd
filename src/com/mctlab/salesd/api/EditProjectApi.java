package com.mctlab.salesd.api;

import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.network.api.post.AbsPostJsonApi;
import com.mctlab.salesd.constant.SalesDUrls;
import com.mctlab.salesd.data.Project;

import org.apache.http.HttpResponse;

/**
 * Created by liqiang on 6/4/14.
 */
public class EditProjectApi extends AbsPostJsonApi<Project, Void> {

    public EditProjectApi(Project project) {
        super(SalesDUrls.getRequestUrl(), project);
    }

    @Override
    protected Void decodeResponse(HttpResponse response) throws DecodeResponseException {
        return null;
    }

    @Override
    protected String apiName() {
        return EditProjectApi.class.getSimpleName();
    }
}
