package com.mctlab.ansight.common.exception;

import java.io.IOException;

public class AsIOException extends IOException {

    private Throwable cause;

    public AsIOException() {
        super();
    }

    public AsIOException(String message, Throwable throwable) {
        this(message);
        this.cause = throwable;
    }

    public AsIOException(String detailMessage) {
        super(detailMessage);
    }

    public AsIOException(Throwable throwable) {
        this("cause=" + throwable);
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

}
