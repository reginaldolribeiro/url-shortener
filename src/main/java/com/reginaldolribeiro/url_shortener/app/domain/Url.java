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
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        if (longUrl == null || longUrl.isBlank())
            throw new IllegalArgumentException("longUrl cannot be null or empty.");

        return new Builder()
                .id(id)
                .longUrl(longUrl)
                .createdDate(LocalDateTime.now(Clock.systemUTC()))
                .user(user)
                .clicks(0)
                .isActive(true)
                .build();
    }

    public void incrementClick() {
        this.clicks += 1;
    }

    public void enable() {
        this.isActive = true;
    }

    public void disable() {
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

    // Private constructor for the builder
    private Url(Builder builder) {
        this.id = builder.id;
        this.longUrl = builder.longUrl;
        this.createdDate = builder.createdDate;
        this.user = builder.user;
        this.clicks = builder.clicks;
        this.isActive = builder.isActive;
    }

    // Static inner Builder class
    public static class Builder {
        private String id;
        private String longUrl;
        private LocalDateTime createdDate;
        private User user;
        private Integer clicks;
        private boolean isActive;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder longUrl(String longUrl) {
            this.longUrl = longUrl;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder clicks(Integer clicks) {
            this.clicks = clicks;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Url build() {
            // Validate mandatory fields if needed
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("ID cannot be null or empty.");
            }
            if (longUrl == null || longUrl.isBlank()) {
                throw new IllegalArgumentException("Long URL cannot be null or empty.");
            }
            return new Url(this);
        }
    }


}
