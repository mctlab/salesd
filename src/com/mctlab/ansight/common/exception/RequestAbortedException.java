package com.mctlab.ansight.common.exception;

public class RequestAbortedException extends ActionAbortedException {

    public RequestAbortedException() {
        super();
    }

    public RequestAbortedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RequestAbortedException(String detailMessage) {
        super(detailMessage);
    }

    public RequestAbortedException(Throwable throwable) {
        super(throwable);
    }

}
