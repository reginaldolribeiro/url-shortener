package com.reginaldolribeiro.url_shortener.adapter.controller.url;

public class UrlNullableException extends RuntimeException {
    public UrlNullableException(){}
    public UrlNullableException(String message){
        super(message);
    }

    public UrlNullableException(String message, Throwable cause) {
        super(message, cause);
    }
}
