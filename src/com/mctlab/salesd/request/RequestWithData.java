package com.mctlab.salesd.request;

import com.mctlab.ansight.common.data.BaseData;

/**
 * Created by liqiang on 6/12/14.
 */
public class RequestWithData extends Request {

    private BaseData data;

    public RequestWithData(String table, String operation, BaseData data) {
        super(table, operation);
        this.data = data;
    }

    public BaseData getData() {
        return data;
    }

    public void setData(BaseData data) {
        this.data = data;
    }
}
