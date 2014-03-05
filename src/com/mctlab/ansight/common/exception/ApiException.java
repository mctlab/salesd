package com.mctlab.ansight.common.exception;

public class ApiException extends AsIOException {

    public ApiException() {
        super();
    }

    public ApiException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public ApiException(Throwable throwable) {
        super(throwable);
    }

}
