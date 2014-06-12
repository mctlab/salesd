package com.mctlab.salesd.request;

/**
 * Created by liqiang on 6/12/14.
 */
public class RequestDelete extends Request {

    private int serverId;

    public RequestDelete(String table, String operation, int serverId) {
        super(table, operation);
        this.serverId = serverId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }
}
