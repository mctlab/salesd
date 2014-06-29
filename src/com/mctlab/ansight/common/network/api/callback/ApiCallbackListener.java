package com.mctlab.ansight.common.network.api.callback;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.RequestAbortedException;

public abstract class ApiCallbackListener<Result> {

    public void onApiStart(int token) {}

    public void onApiAborted(int token, RequestAbortedException exception) {}

    public void onApiSuccess(int token, Result result) {}

    public void onApiFailed(int token, ApiException exception) {}

    public void onApiFinish(int token) {}

}
