package com.reginaldolribeiro.url_shortener.app.exception;

public class UrlNullableException extends BusinessException {
    public UrlNullableException(){}
    public UrlNullableException(String message){
        super(message);
    }

    public UrlNullableException(String message, Throwable cause) {
        super(message, cause);
    }
}
