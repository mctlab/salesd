package com.mctlab.ansight.common.network.http;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.RequestAbortedException;

/**
 * wrap the api result:
 * 1. success
 * 2. error (ApiException throwed)
 * 3. aborted (RequestAbortedException throwed)
 */
public class HttpTaskResult<Result> {

    public final Result result;
    public final ApiException apiException;
    public final boolean success;
    public final boolean aborted;
    public final RequestAbortedException abortedException;

    /**
     * success
     */
    public HttpTaskResult(Result result) {
        this.result = result;
        apiException = null;
        success = true;
        aborted = false;
        abortedException = null;
    }

    /**
     * error
     */
    public HttpTaskResult(ApiException exception) {
        result = null;
        this.apiException = exception;
        success = false;
        aborted = false;
        abortedException = null;
    }

    /**
     * aborted
     */
    public HttpTaskResult(RequestAbortedException exception) {
        result = null;
        this.apiException = null;
        success = false;
        aborted = true;
        abortedException = exception;
    }

}
