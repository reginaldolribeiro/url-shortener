package com.reginaldolribeiro.url_shortener.app.exception;

public class InvalidUrlException extends BusinessException {
    public InvalidUrlException(){}
    public InvalidUrlException(String message){
        super(message);
    }

    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
