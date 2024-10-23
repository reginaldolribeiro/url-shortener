package com.reginaldolribeiro.url_shortener.adapter.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
public class UrlMappings implements Serializable {
    private String shortUrlId;
    private String longUrl;
    private String createdDate;
    private String userId;
    private int clicks;
    private boolean isActive;

    public static UrlMappings create(String shortUrlId, String longUrl, String userId){
        return new UrlMappings(shortUrlId, longUrl, userId);
    }

    private UrlMappings(String shortUrlId, String longUrl, String userId) {
        this.shortUrlId = shortUrlId;
        this.longUrl = longUrl;
        this.createdDate = LocalDateTime.now(Clock.systemUTC()).toString();
        this.userId = userId;
        this.clicks = 0;
        this.isActive = true;
    }
}
