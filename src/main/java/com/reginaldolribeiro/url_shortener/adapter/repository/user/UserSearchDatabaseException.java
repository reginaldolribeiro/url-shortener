package com.reginaldolribeiro.url_shortener.adapter.repository.user;

public class UserSearchDatabaseException extends RuntimeException {
    public UserSearchDatabaseException(){}
    public UserSearchDatabaseException(String message){
        super(message);
    }

    public UserSearchDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
