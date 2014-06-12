package com.mctlab.salesd.request;

import com.mctlab.ansight.common.data.BaseData;

/**
 * Created by liqiang on 6/12/14.
 */
public class Request extends BaseData {

    private String table;
    private String operation;
    private String info;

    public Request(String table, String operation) {
        this.table = table;
        this.operation = operation;
    }

    public Request(String table, String operation, String info) {
        this.table = table;
        this.operation = operation;
        this.info = info;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
