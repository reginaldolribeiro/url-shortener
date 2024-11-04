package com.reginaldolribeiro.url_shortener.app.domain;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

public class Url implements Serializable {
    private String id;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
    private Integer clicks;
    private boolean active;

    public static Url create(String id, String longUrl, User user) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        if (longUrl == null || longUrl.isBlank())
            throw new IllegalArgumentException("longUrl cannot be null or empty.");

        return new Builder()
                .id(id)
                .longUrl(longUrl)
                .createdAt(LocalDateTime.now(Clock.systemUTC()))
                .updatedAt(LocalDateTime.now(Clock.systemUTC()))
                .user(user)
                .clicks(0)
                .active(true)
                .build();
    }

    public void incrementClick() {
        this.clicks += 1;
    }

    public void enable() {
        this.active = true;
    }

    public void disable() {
        this.active = false;
    }

    public String getId() {
        return id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public Integer getClicks() {
        return clicks;
    }


    public boolean isActive() {
        return active;
    }

    // Private constructor for the builder
    private Url(Builder builder) {
        this.id = builder.id;
        this.longUrl = builder.longUrl;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.user = builder.user;
        this.clicks = builder.clicks;
        this.active = builder.active;
    }

    // Static inner Builder class
    public static class Builder {
        private String id;
        private String longUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private User user;
        private Integer clicks;
        private boolean active;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder longUrl(String longUrl) {
            this.longUrl = longUrl;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
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

        public Builder active(boolean active) {
            this.active = active;
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
