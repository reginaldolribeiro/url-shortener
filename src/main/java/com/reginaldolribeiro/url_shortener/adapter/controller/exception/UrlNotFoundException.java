package com.reginaldolribeiro.url_shortener.adapter.controller.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(){}
    public UrlNotFoundException(String message){
        super(message);
    }

    public UrlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
