package com.mctlab.ansight.common.exception;

import java.io.IOException;

public class AsException extends IOException {

    private Throwable cause;

    public AsException() {
        super();
    }

    public AsException(String message, Throwable throwable) {
        this(message);
        this.cause = throwable;
    }

    public AsException(String detailMessage) {
        super(detailMessage);
    }

    public AsException(Throwable throwable) {
        this("cause=" + throwable);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

}
