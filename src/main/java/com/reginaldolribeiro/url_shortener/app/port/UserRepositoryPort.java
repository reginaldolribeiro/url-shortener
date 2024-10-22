package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> get(String userId);
}
