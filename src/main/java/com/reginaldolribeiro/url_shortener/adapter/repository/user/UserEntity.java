package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserEntity(
        String id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) implements Serializable {

    static User fromMapping(String id,
                            String name,
                            String email,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            boolean active) {
        return new User.Builder()
                .id(UUID.fromString(id))
                .name(name)
                .email(email)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .active(active)
                .build();
    }

}
