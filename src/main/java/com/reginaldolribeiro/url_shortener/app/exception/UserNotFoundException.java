package com.reginaldolribeiro.url_shortener.app.exception;

public class UserNotFoundException extends BusinessException{
    public UserNotFoundException(){}
    public UserNotFoundException(String message){
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
