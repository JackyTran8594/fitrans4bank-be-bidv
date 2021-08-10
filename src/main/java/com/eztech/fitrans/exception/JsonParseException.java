package com.eztech.fitrans.exception;

public class JsonParseException extends RuntimeException {

    public JsonParseException(String message, Throwable e) {
        super(message, e);
    }
}
