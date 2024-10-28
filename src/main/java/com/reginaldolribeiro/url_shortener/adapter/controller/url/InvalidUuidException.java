package com.reginaldolribeiro.url_shortener.adapter.controller.url;

public class InvalidUuidException extends RuntimeException {
    public InvalidUuidException(){}
    public InvalidUuidException(String message){
        super(message);
    }

    public InvalidUuidException(String message, Throwable cause) {
        super(message, cause);
    }
}
