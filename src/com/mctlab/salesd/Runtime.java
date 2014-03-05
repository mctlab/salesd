package com.mctlab.salesd;

import com.mctlab.ansight.common.AsAppConfig;
import com.mctlab.ansight.common.AsRuntime;
import com.mctlab.ansight.common.exception.HttpStatusException;

public class Runtime extends AsRuntime {

    public static void init() {
        if (me == null) {
            synchronized (AsRuntime.class) {
                if (me == null) {
                    me = new Runtime();
                }
            }
        }
    }

    public static Runtime getInstance() {
        return (Runtime) AsRuntime.getInstance();
    }

    @Override
    public void onNetworkNotAvailable() {
        toast(R.string.network_not_available);
    }

    @Override
    public void onRequestTimeout() {
        toast(R.string.tip_load_failed_network_error);
    }

    @Override
    public void onHttpStatusCodeError(HttpStatusException error) {
        int code = error.getStatusCode();
        if (code == 401) {
            // TODO: authentication failed
        } else if (code >= 500 && code < 600) {
            // server error
            toast(R.string.tip_load_failed_server_error);
        } else {
            if (AsAppConfig.getInstance().isDebug()) {
                toast(String.format(getString(R.string.network_error_with_status_code), code));
            } else {
                toast(R.string.tip_load_failed_network_error);
            }
        }
    }

}
