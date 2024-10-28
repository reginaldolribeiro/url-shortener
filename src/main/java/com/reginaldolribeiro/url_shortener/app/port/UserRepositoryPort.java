package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(String userId);
    void save(User user);
}
