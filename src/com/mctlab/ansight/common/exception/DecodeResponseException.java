package com.mctlab.ansight.common.exception;

public class DecodeResponseException extends ApiException {

    public DecodeResponseException() {
        super();
    }

    public DecodeResponseException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DecodeResponseException(String detailMessage) {
        super(detailMessage);
    }

    public DecodeResponseException(Throwable throwable) {
        super(throwable);
    }

}
