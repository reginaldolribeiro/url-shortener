package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserDatabaseRepository implements UserRepositoryPort {

    @Override
    public Optional<User> get(String userId) {
        if(userId.equals("1") || userId.equals("9b8a2db5-e50a-481f-9e9a-828c37e721c1")){
            return Optional.of(
                    new User(UUID.randomUUID(), "User1", "user1@gmail.com")
            );
        }

        return Optional.empty();
    }
}
