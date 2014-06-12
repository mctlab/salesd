package com.mctlab.salesd.request;

import com.mctlab.ansight.common.data.BaseData;

/**
 * Created by liqiang on 6/12/14.
 */
public class RequestInsert extends RequestWithData {

    public RequestInsert(String table, String operation, BaseData data) {
        super(table, operation, data);
    }
}
