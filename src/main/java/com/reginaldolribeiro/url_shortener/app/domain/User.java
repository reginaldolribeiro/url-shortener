package com.reginaldolribeiro.url_shortener.app.domain;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class User implements Serializable {
    private UUID id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public static User create(String name, String email){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        return new Builder()
                .id(UUID.randomUUID())
                .name(name)
                .email(email)
                .createdAt(LocalDateTime.now(Clock.systemUTC()))
                .updatedAt(LocalDateTime.now(Clock.systemUTC()))
                .active(true)
                .build();
    }

    // Private constructor for the builder
    private User(Builder builder) {
        id = builder.id;
        name = builder.name;
        email = builder.email;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
        active = builder.active;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean active;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or empty.");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or empty.");
            }
            return new User(this);
        }
    }
}
