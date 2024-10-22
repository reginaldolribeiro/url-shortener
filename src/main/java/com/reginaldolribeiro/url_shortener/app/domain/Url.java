package com.reginaldolribeiro.url_shortener.app.domain;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

public class Url {
    private String id;
    private String longUrl;
    private LocalDateTime createdDate;
    private User user;
    private Integer clicks;
    private boolean isActive;

    private Url(String id, String longUrl, LocalDateTime createdDate, User user, Integer clicks, boolean isActive){
        this.id = id;
        this.longUrl = longUrl;
        this.user = user;
    }

    public static Url create(String id, String longUrl, User user) {
        if(id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        if(longUrl == null || longUrl.isBlank())
            throw new IllegalArgumentException("longUrl cannot be null or empty.");

        return new Url(id, longUrl, LocalDateTime.now(UTC), user, 0, true);
    }

    public String getId() {
        return id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public User getUser() {
        return user;
    }

    public Integer getClicks() {
        return clicks;
    }

    public boolean isActive() {
        return isActive;
    }
}
