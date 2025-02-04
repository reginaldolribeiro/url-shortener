package com.reginaldolribeiro.url_shortener.adapter.controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(int status, String message, Map<String, String> errors, LocalDateTime timestamp){
    public ErrorResponse(int status, String message){
        this(status, message, Map.of("message", message), LocalDateTime.now(Clock.systemUTC()));
    }

    public ErrorResponse(int status, String message, Map<String, String> errors){
        this(status, message, errors, LocalDateTime.now(Clock.systemUTC()));
    }
}