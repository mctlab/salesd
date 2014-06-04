package com.mctlab.ansight.common.network.api;

import com.mctlab.ansight.common.AsAppConfig;
import com.mctlab.ansight.common.AsRuntime;
import com.mctlab.ansight.common.DeviceConfig;
import com.mctlab.ansight.common.activity.AsActivity;
import com.mctlab.ansight.common.exception.ApiException;
import com.mctlab.ansight.common.exception.DecodeResponseException;
import com.mctlab.ansight.common.exception.HttpStatusException;
import com.mctlab.ansight.common.exception.NetworkNotAvailableException;
import com.mctlab.ansight.common.exception.RequestAbortedException;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.network.http.AsyncHttpExecutor;
import com.mctlab.ansight.common.network.http.AsyncHttpExecutor.Priority;
import com.mctlab.ansight.common.network.http.HttpDeleteTask;
import com.mctlab.ansight.common.network.http.HttpGetTask;
import com.mctlab.ansight.common.network.http.HttpHeadTask;
import com.mctlab.ansight.common.network.http.HttpPostTask;
import com.mctlab.ansight.common.network.http.HttpPutTask;
import com.mctlab.ansight.common.network.http.HttpTask;
import com.mctlab.ansight.common.network.http.HttpTaskResult;
import com.mctlab.ansight.common.util.ExceptionUtils;
import com.mctlab.ansight.common.util.HttpUtils;
import com.mctlab.ansight.common.util.L;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.lang.ref.WeakReference;

public abstract class AbstractApi<Form extends IForm, Result> implements IServerApi<Result> {

    protected final Form form;
    protected final String baseUrl;

    private AsyncHttpExecutor<Result> executor;

    private WeakReference<AsActivity> activityRef;
    private WeakReference<HttpUriRequest> requestRef;

    private boolean isAborted = false;

    protected AbstractApi(String baseUrl, Form form) {
        this.form = form;
        this.baseUrl = baseUrl;
    }

    @Override
    public void call(AsActivity activity) {
        Result cachedResult = getCachedResult();
        if (cachedResult != null) {
            executorCallback.onSuccess(cachedResult);
            executorCallback.onFinish();
            return;
        }
        if (activity != null) {
            activityRef = new WeakReference<AsActivity>(activity);
        }
        executor = new AsyncHttpExecutor<Result>();
        HttpTask<Result> task = onCreateTask();
        if (activity == null) {
            executor.execTask(task, executorCallback, Priority.BACKGROUND);
        } else {
            executor.execTask(task, executorCallback, Priority.FORE_GROUND);
        }
    }

    @Override
    public Result syncCall(AsActivity activity) throws ApiException, RequestAbortedException {
        Result cachedResult = getCachedResult();
        if (cachedResult != null) {
            return cachedResult;
        }
        if (activity != null) {
            activityRef = new WeakReference<AsActivity>(activity);
        }
        HttpTask<Result> task = onCreateTask();
        final HttpTaskResult<Result> result = task.exec();
        if (result.aborted) {
            throw result.abortedException;
        }
        if (isAborted) {
            throw new RequestAbortedException();
        }
        if (result.success) {
            return result.result;
        }
        AsRuntime.getInstance().postRunnable(new Runnable() {
            @Override
            public void run() {
                onApiException(result.apiException);
            }
        });
        throw result.apiException;
    }

    protected final HttpGetTask<Result> newHttpGetTask() {
        return new HttpGetTask<Result>(baseUrl, form, executorCallback);
    }

    protected final HttpPostTask<Result> newHttpPostTask() {
        return new HttpPostTask<Result>(baseUrl, form, executorCallback);
    }

    protected final HttpPutTask<Result> newHttpPutTask() {
        return new HttpPutTask<Result>(baseUrl, form, executorCallback);
    }

    protected final HttpDeleteTask<Result> newHttpDeleteTask() {
        return new HttpDeleteTask<Result>(baseUrl, form, executorCallback);
    }

    protected final HttpHeadTask newHttpHeadTask() {
        return new HttpHeadTask(baseUrl, form, (ExecutorCallback<Void>) executorCallback);
    }

    /**
     * process the request before calling HttpClient.execute(request)
     */
    protected void onPreProcess(HttpUriRequest request) {
        HttpUtils.setCookies(request);
        requestRef = new WeakReference<HttpUriRequest>(request);
        AsActivity activity = getActivity();
        if (activity != null) {
            activity.getRequestManager().register(this);
        }
    }

    public AsActivity getActivity() {
        return activityRef == null ? null : activityRef.get();
    }

    public HttpUriRequest getRequest() {
        return requestRef == null ? null : requestRef.get();
    }

    @Override
    public boolean cancel() {
        isAborted = true;
        HttpUriRequest request = getRequest();
        if (request != null) {
            L.i("socket", "before abort request: " + request.getURI());
            request.abort();
            L.i("socket", "after abort request: " + request.getURI());
            return true;
        }
        return false;
    }

    /**
     * process the HttpResponse after HttpClient.execute(request) done
     */
    protected void onPostProcess(HttpResponse response) {
        HttpUtils.saveCookies(response);
        AsActivity activity = getActivity();
        if (activity != null) {
            activity.getRequestManager().unregister(this);
        }
        requestRef = null;
    }

    /**
     * process the url before calling the api return the new Url
     */
    protected String onPreProcessUrl(String url) {
        String versionName = AsAppConfig.getInstance().getVersionName();
        int platformCode = DeviceConfig.getInstance().getPlatformCode();
        if (url.contains("?")) {
            return String.format("%s&platform=android%d&version=%s", url, platformCode, versionName);
        } else {
            return String.format("%s?platform=android%d&version=%s", url, platformCode, versionName);
        }
    }

    private void onApiException(ApiException exception) {
        if (exception instanceof NetworkNotAvailableException) {
            NetworkNotAvailableException e = (NetworkNotAvailableException) exception;
            /**
             * exception has been handled. do not pass it to AbstractApi2.this.onFailed(exception)
             **/
            if (AbstractApi.this.onNetworkNotAvailable(e)) {
                return;
            }
        }
        if (exception instanceof HttpStatusException) {
            HttpStatusException e = (HttpStatusException) exception;
            /**
             * exception has been handled. do not pass it to AbstractApi2.this.onFailed(exception)
             **/
            if (AbstractApi.this.onHttpStatusException(e)) {
                return;
            }
        }
        if (ExceptionUtils.causedByTimeout(exception)) {
            if (AbstractApi.this.onRequestTimeout(exception)) {
                return;
            }
        }
        AbstractApi.this.onFailed(exception);
    }

    /**
     * return true if the exception has been handled.
     */
    protected boolean onNetworkNotAvailable(final NetworkNotAvailableException exception) {
        AsRuntime.getInstance().onNetworkNotAvailable();
        return false;
    }

    protected boolean onRequestTimeout(Throwable exception) {
        AsActivity activity = getActivity();
        if (activity != null) {
            AsRuntime.getInstance().onRequestTimeout();
        }
        return true;
    }

    /**
     * return true if the exception has been handled.
     */
    protected boolean onHttpStatusException(final HttpStatusException exception) {
        L.w(this, getClass().getSimpleName() + "\n" + exception);
        int statusCode = exception.getStatusCode();
        // TODO: handle statusCode
        AsRuntime.getInstance().postRunnable(new Runnable() {
            @Override
            public void run() {
                AsRuntime.getInstance().onHttpStatusCodeError(exception);
            }
        });
        return false;
    }

    protected abstract Result decodeResponse(HttpResponse response) throws DecodeResponseException;

    protected abstract HttpTask<Result> onCreateTask();

    protected void onSuccess(Result result) {}

    protected void onFailed(ApiException exception) {}

    protected void onStart() {
        // TODO: start loading
    }

    protected void onFinish() {
        // TODO: end loading
    }

    /**
     * do cache data here...
     */
    protected void afterDecode(Result result) {}

    protected void onAborted(RequestAbortedException exception) {
        L.i(this, exception);
    }

    /**
     * return the name of the api. used by Statistics
     */
    protected abstract String apiName();

    //region cache

    public Result getCachedResult() {
        return null;
    }

    //endregion

    private ExecutorCallback<Result> executorCallback = new ExecutorCallback<Result>() {

        @Override
        public boolean isAborted() {
            return AbstractApi.this.isAborted;
        }

        private boolean isActivityDestroyed() {
            if (activityRef == null) { // background api
                return false;
            }
            AsActivity activity = activityRef.get();
            if (activity == null || activity.getContextDelegate().isActivityDestroyed()) {
                return true;
            }
            return false;
        }

        @Override
        public void onSuccess(Result result) {
            if (isActivityDestroyed()) {
                L.w(this, "the activity has been destroyed");
                try {
                    AbstractApi.this.onSuccess(result);
                } catch (Throwable e) {
                    L.e(this, e);
                }
            } else {
                AbstractApi.this.onSuccess(result);
            }
        }

        @Override
        public void onFailed(ApiException exception) {
            if (isActivityDestroyed()) {
                L.w(this, "the activity has been destroyed");
                try {
                    AbstractApi.this.onApiException(exception);
                } catch (Throwable e) {
                    L.e(this, e);
                }
            } else {
                AbstractApi.this.onApiException(exception);
            }
        }

        @Override
        public void onAborted(RequestAbortedException exception) {
            if (isActivityDestroyed()) {
                L.w(this, "the activity has been destroyed");
                try {
                    AbstractApi.this.onAborted(exception);
                } catch (Throwable e) {
                    L.e(this, e);
                }
            } else {
                AbstractApi.this.onAborted(exception);
            }
        }

        @Override
        public String preProcessUrl(String url) {
            return AbstractApi.this.onPreProcessUrl(url);
        }

        @Override
        public void preProcess(HttpUriRequest request) {
            AbstractApi.this.onPreProcess(request);
        }

        @Override
        public void postProcess(HttpResponse response) {
            AbstractApi.this.onPostProcess(response);
        }

        @Override
        public String getApiName() {
            return AbstractApi.this.apiName();
        }

        @Override
        public void onStart() {
            AbstractApi.this.onStart();
        }

        @Override
        public void onFinish() {
            if (isActivityDestroyed()) {
                L.w(this, "the activity has been destroyed");
                try {
                    AbstractApi.this.onFinish();
                } catch (Throwable e) {
                    L.e(this, e);
                }
            } else {
                AbstractApi.this.onFinish();
            }
        }

        @Override
        public Result decodeResponse(HttpResponse response) throws DecodeResponseException {
            return AbstractApi.this.decodeResponse(response);
        }

        @Override
        public void afterDecode(Result data) {
            AbstractApi.this.afterDecode(data);
        }
    };
}
