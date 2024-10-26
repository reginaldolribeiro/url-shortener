package com.reginaldolribeiro.url_shortener.app.exception;

public class ShortUrlMalformedException extends BusinessException {
    public ShortUrlMalformedException(){}
    public ShortUrlMalformedException(String message){
        super(message);
    }

    public ShortUrlMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
