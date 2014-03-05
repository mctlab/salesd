package com.mctlab.ansight.common.exception;

public class NetworkNotAvailableException extends ApiException {

    public NetworkNotAvailableException() {
        super();
    }

    public NetworkNotAvailableException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NetworkNotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    public NetworkNotAvailableException(Throwable throwable) {
        super(throwable);
    }

}
