package com.mctlab.ansight.common.exception;

public class JsonException extends DecodeResponseException {

    public JsonException() {
        super();
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String detailMessage) {
        super(detailMessage);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

}
