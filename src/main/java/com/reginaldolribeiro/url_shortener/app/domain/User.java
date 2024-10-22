package com.reginaldolribeiro.url_shortener.app.domain;

import java.util.UUID;

public record User (
        UUID id,
        String name,
        String email
){}
