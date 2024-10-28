package com.reginaldolribeiro.url_shortener.adapter.repository.user;

public class UserSaveDatabaseException extends RuntimeException {
    public UserSaveDatabaseException(){}
    public UserSaveDatabaseException(String message){
        super(message);
    }

    public UserSaveDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
