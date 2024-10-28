package com.reginaldolribeiro.url_shortener.adapter.repository.url;

public class UrlSaveDatabaseException extends RuntimeException {
    public UrlSaveDatabaseException(){}
    public UrlSaveDatabaseException(String message){
        super(message);
    }

    public UrlSaveDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
