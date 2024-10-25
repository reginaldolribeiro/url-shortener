package com.reginaldolribeiro.url_shortener.adapter.controller.exception;

public class UrlDisabledException extends RuntimeException {
    public UrlDisabledException(){}
    public UrlDisabledException(String message){
        super(message);
    }

    public UrlDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}