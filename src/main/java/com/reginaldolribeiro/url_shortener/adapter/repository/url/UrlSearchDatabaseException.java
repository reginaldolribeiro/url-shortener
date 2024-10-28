package com.reginaldolribeiro.url_shortener.adapter.repository.url;

public class UrlSearchDatabaseException extends RuntimeException {
    public UrlSearchDatabaseException(){}
    public UrlSearchDatabaseException(String message){
        super(message);
    }

    public UrlSearchDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
