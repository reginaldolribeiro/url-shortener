package com.reginaldolribeiro.url_shortener.app.domain;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

public class Url implements Serializable {
    private String id;
    private String longUrl;
    private LocalDateTime createdDate;
    private User user;
    private Integer clicks;
    private boolean isActive;

    public static Url create(String id, String longUrl, User user) {
        if(id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        if(longUrl == null || longUrl.isBlank())
            throw new IllegalArgumentException("longUrl cannot be null or empty.");

        return new Url(id, longUrl, LocalDateTime.now(Clock.systemUTC()), user, 0, true);
    }

    public void incrementClick(){
        this.clicks += 1;
    }

    public void enable(){
        this.isActive = true;
    }

    public void disable(){
        this.isActive = false;
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

    private Url(String id, String longUrl, LocalDateTime createdDate, User user, Integer clicks, boolean isActive){
        this.id = id;
        this.longUrl = longUrl;
        this.user = user;
        this.createdDate = createdDate;
        this.clicks = clicks;
        this.isActive = isActive;
    }

}
