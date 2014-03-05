package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.network.api.IServerApi;
import com.mctlab.ansight.common.util.L;

import java.util.HashSet;
import java.util.Set;

public class RequestManager {

    private Set<IServerApi<?>> runningApi = new HashSet<IServerApi<?>>();

    public synchronized void register(IServerApi<?> api) {
        runningApi.add(api);
    }

    public synchronized void unregister(IServerApi<?> api) {
        runningApi.remove(api);
    }

    public synchronized void cancelAll() {
        L.i(this, "cancelAll()");
        try {
            for (IServerApi<?> api : runningApi) {
                api.cancel();
            }
            runningApi.clear();
        } catch (Throwable e) {
            L.e(this, e);
        }
    }

}
