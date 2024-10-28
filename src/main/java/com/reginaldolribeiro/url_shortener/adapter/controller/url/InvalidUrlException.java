package com.reginaldolribeiro.url_shortener.adapter.controller.url;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(){}
    public InvalidUrlException(String message){
        super(message);
    }

    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
