package com.mctlab.ansight.common.network.api.callback;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.HttpStatusException;

public interface ApiCallback<Result> {

    public void onStart();

    public void onFinish();

    public void afterDecode(Result result);

    public void onSuccess(Result result);

    public void onFailed(ApiException exception);

    public boolean onHttpStatusException(HttpStatusException exception);

}
