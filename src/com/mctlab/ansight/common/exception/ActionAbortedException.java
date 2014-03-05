package com.mctlab.ansight.common.exception;

public class ActionAbortedException extends AsException {

    public ActionAbortedException() {
        super();
    }

    public ActionAbortedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ActionAbortedException(String detailMessage) {
        super(detailMessage);
    }

    public ActionAbortedException(Throwable throwable) {
        super(throwable);
    }

}
