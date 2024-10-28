package com.reginaldolribeiro.url_shortener.app.usecase.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateUserOutput(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
