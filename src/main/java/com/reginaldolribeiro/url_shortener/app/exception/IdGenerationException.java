package com.reginaldolribeiro.url_shortener.app.exception;

public class IdGenerationException extends BusinessException {
    public IdGenerationException(){}
    public IdGenerationException(String message){
        super(message);
    }

    public IdGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
