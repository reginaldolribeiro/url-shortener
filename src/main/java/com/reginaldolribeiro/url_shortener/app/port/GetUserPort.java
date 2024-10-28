package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.util.UUID;

public interface GetUserPort {
    User findById(UUID id);
}
