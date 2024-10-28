package com.reginaldolribeiro.url_shortener.adapter.controller.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
