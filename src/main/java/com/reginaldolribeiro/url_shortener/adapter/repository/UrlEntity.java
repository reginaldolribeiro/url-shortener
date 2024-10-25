package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UrlEntity(
        String shortUrlId,
        String longUrl,

//        @JsonSerialize(using = LocalDateSerializer.class)
//        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDateTime createdDate,

        String userId,
        int clicks,
        boolean isActive
) implements Serializable {

    static Url fromMapping(String shortUrlId,
                           String longUrl,
                           LocalDateTime createdDate,
                           User user,
                           int clicks,
                           boolean isActive) {
        return new Url.Builder()
                .id(shortUrlId)
                .longUrl(longUrl)
                .createdDate(createdDate)
                .user(user)
                .clicks(clicks)
                .isActive(isActive)
                .build();
    }

    // Make the fromMapping method package-private
//    static Url fromMapping(String id, String longUrl, User user, LocalDateTime createdDate, Integer clicks, boolean isActive) {
//        return new Url(id, longUrl, createdDate, user, clicks, isActive);
//    }


//    public static UrlEntity create(String shortUrlId, String longUrl, String userId){
//        return new UrlEntity(shortUrlId, longUrl, userId);
//    }
//
//    private UrlEntity(String shortUrlId, String longUrl, String userId) {
//        this.shortUrlId = shortUrlId;
//        this.longUrl = longUrl;
//        this.createdDate = LocalDateTime.now(Clock.systemUTC()).toString();
//        this.userId = userId;
//        this.clicks = 0;
//        this.isActive = true;
//    }
}
