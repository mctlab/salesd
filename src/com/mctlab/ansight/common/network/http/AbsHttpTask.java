package com.mctlab.ansight.common.network.http;

import android.net.http.AndroidHttpClient;

import com.mctlab.ansight.common.AsAppConfig;
import com.mctlab.ansight.common.AsApplication;
import com.mctlab.ansight.common.DeviceConfig;
import com.mctlab.ansight.common.exception.*;
import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.network.form.IForm;
import com.mctlab.ansight.common.util.ExceptionUtils;
import com.mctlab.ansight.common.util.HttpUtils;
import com.mctlab.ansight.common.util.L;
import com.mctlab.ansight.common.util.StringUtils;

import org.apache.http.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbsHttpTask<Result> implements HttpTask<Result> {

    private final String baseUrl;

    protected final ExecutorCallback<Result> callback;
    protected final IForm form;

    public AbsHttpTask(String baseUrl, IForm form, ExecutorCallback<Result> callback) {
        super();
        this.callback = callback;
        this.baseUrl = baseUrl;
        this.form = form;
    }

    @Override
    public final HttpTaskResult<Result> exec() {
//        AndroidHttpClient client = HttpClientInstance.newInstance();
        try {
            Result result = fakeExec();
//            Result result = innerExec(client);
            return wrapResult(result);
        } catch (ApiException e) {
            return wrapResult(e);
        } catch (RequestAbortedException e) {
            return wrapResult(e);
        } finally {
//            client.close();
        }
    }

    private Result fakeExec() throws ApiException, RequestAbortedException {
        final HttpUriRequest request = onCreateRequest();
        callback.preProcess(request);
        beforeExecute(request);
        HttpResponse response;
        try {
            L.d("socket", "before exec: " + request.getURI());
            response = new BasicHttpResponse(new StatusLine() {

                @Override
                public ProtocolVersion getProtocolVersion() {
                    return null;
                }

                @Override
                public int getStatusCode() {
                    return 200;
                }

                @Override
                public String getReasonPhrase() {
                    return null;
                }
            });
            HttpEntity httpEntity = new HttpEntity() {

                @Override
                public boolean isRepeatable() {
                    return false;
                }

                @Override
                public boolean isChunked() {
                    return false;
                }

                @Override
                public long getContentLength() {
                    return 0;
                }

                @Override
                public Header getContentType() {
                    return null;
                }

                @Override
                public Header getContentEncoding() {
                    return null;
                }

                @Override
                public InputStream getContent() throws IOException, IllegalStateException {
                    return loadUrlFromResource(request.getURI().getPath());
                }

                @Override
                public void writeTo(OutputStream outputStream) throws IOException {

                }

                @Override
                public boolean isStreaming() {
                    return false;
                }

                @Override
                public void consumeContent() throws IOException {

                }
            };
            response.setEntity(httpEntity);
            L.d("socket", "after exec: " + request.getURI());
        } catch (Throwable e) {
            if (ExceptionUtils.causedByRequestAborted(e)) {
                throw new RequestAbortedException(e);
            }
            throw new ApiException(e);
        }
        afterExecute(response);
        callback.postProcess(response);
        Result result = callback.decodeResponse(response);
        callback.afterDecode(result);
        return result;
    }

    private InputStream loadUrlFromResource(String path) {
        L.i(this, path);
        if (StringUtils.isBlank(path)) {
            return null;
        }
        String fileName = path.replaceAll("/", ".");
        if (fileName.startsWith(".")) {
            fileName = fileName.substring(1);
        }
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        AsApplication app = AsApplication.getInstance();
        try {
            return app.getResources().getAssets().open(fileName);
        } catch (Exception e) {
            L.e(this, e);
            return null;
        }
    }

    private Result innerExec(AndroidHttpClient client) throws ApiException, RequestAbortedException {
        if (!DeviceConfig.getInstance().isNetworkAvailable()) {
            throw new NetworkNotAvailableException();
        }
        HttpUriRequest request = onCreateRequest();
        callback.preProcess(request);
        beforeExecute(request);
        HttpResponse response;
        try {
            L.d("socket", "before exec: " + request.getURI());
            response = client.execute(request);
            L.d("socket", "after exec: " + request.getURI());
        } catch (Throwable e) {
            if (ExceptionUtils.causedByRequestAborted(e)) {
                throw new RequestAbortedException(e);
            }
            throw new ApiException(e);
        }
        afterExecute(response);
        callback.postProcess(response);
        Result result = callback.decodeResponse(response);
        callback.afterDecode(result);
        return result;
    }

    private void beforeExecute(HttpUriRequest request) {
        if (AsAppConfig.getInstance().isDebug()) {
            L.i(this, String.format("%s[url:%s ,method:%s]", callback.getApiName(), request.getURI().toString(), request.getMethod()));
            if (request.getMethod().equalsIgnoreCase("post")) {
                L.i(this, "post form = " + form);
            }
        }
        request.addHeader("Accept-Encoding", "gzip");
    }

    private void afterExecute(HttpResponse response) throws HttpStatusException, DecodeResponseException {
        int status = HttpUtils.getStatusCode(response);
        if (status < 200 || status >= 300) {
            throw new HttpStatusException(status, response);
        }
    }

    private String processedUrl;

    protected String getUrl() {
        if (processedUrl == null) {
            processedUrl = callback.preProcessUrl(baseUrl);
        }
        return processedUrl;
    }

    protected final HttpTaskResult<Result> wrapResult(Result result) {
        return new HttpTaskResult<Result>(result);
    }

    protected final HttpTaskResult<Result> wrapResult(ApiException e) {
        return new HttpTaskResult<Result>(e);
    }

    protected final HttpTaskResult<Result> wrapResult(RequestAbortedException e) {
        return new HttpTaskResult<Result>(e);
    }

    protected abstract HttpUriRequest onCreateRequest();

}
