package com.mctlab.ansight.common.network.api;

import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.RequestAbortedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface ExecutorCallback<T> {

    public boolean isAborted();

    /**
     * success callback, running on UI thread.
     */
    public void onSuccess(T result);

    /**
     * failed callback, running on UI thread
     */
    public void onFailed(ApiException exception);

    public void onAborted(RequestAbortedException exception);

    /**
     * process the url before calling the api, return the new Url
     */
    public String preProcessUrl(String url);

    /**
     * process the request before calling HttpClient.execute(request)
     */
    public void preProcess(HttpUriRequest request);

    /**
     * process the HttpResponse after HttpClient.execute(request) done
     */
    public void postProcess(HttpResponse response);

    /**
     * return the name of the api. used by Flurry
     */
    public String getApiName();

    public void onStart();

    public void onFinish();

    public T decodeResponse(HttpResponse response) throws DecodeResponseException;

    public void afterDecode(T data);

}
