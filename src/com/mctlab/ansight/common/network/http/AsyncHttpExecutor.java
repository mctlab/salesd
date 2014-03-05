package com.mctlab.ansight.common.network.http;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.mctlab.ansight.common.DeviceConfig;
import com.mctlab.ansight.common.exception.RequestAbortedException;
import com.mctlab.ansight.common.network.api.ExecutorCallback;
import com.mctlab.ansight.common.util.L;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncHttpExecutor<Result> {

    private WrapTask wrapTask;
    private ExecutorCallback<Result> callback;
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static enum Priority {
        FORE_GROUND, BACKGROUND
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    public synchronized void execTask(HttpTask<Result> task, ExecutorCallback<Result> callback, Priority priority) {
        this.callback = callback;
        wrapTask = new WrapTask();
        if (priority == Priority.FORE_GROUND
                // AsyncTask.executeOnExecutor is not available in pre-sdk-11
                && DeviceConfig.getInstance().getPlatformCode() >= 11) {
            wrapTask.executeOnExecutor(EXECUTOR, task);
        } else {
            wrapTask.execute(task);
        }
    }

    private class WrapTask extends AsyncTask<HttpTask<Result>, Void, HttpTaskResult<Result>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callback.onStart();
        }

        @Override
        protected HttpTaskResult<Result> doInBackground(HttpTask<Result>... tasks) {
            HttpTask<Result> task = tasks[0];
            return task.exec();
        }

        @Override
        protected void onPostExecute(HttpTaskResult<Result> result) {
            try {
                if (!isCanceled) {
                    if (result.aborted) {
                        callback.onAborted(result.abortedException);
                    } else if (callback.isAborted()) {
                        callback.onAborted(new RequestAbortedException());
                    } else if (result.success) {
                        callback.onSuccess(result.result);
                    } else {
                        callback.onFailed(result.apiException);
                    }
                } else {
                    callback.onAborted(new RequestAbortedException());
                }
                wrapTask = null;
                isCanceled = false;
            } finally {
                callback.onFinish();
            }
        }
    }

    private volatile boolean isCanceled = false;

    public synchronized void cancel() {
        try {
            if (wrapTask != null && !wrapTask.isCancelled()) {
                wrapTask.cancel(true);
            }
            isCanceled = true;
        } catch (Throwable e) {
            L.e(this, e);
        }
    }

}
