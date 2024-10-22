package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserRepositoryDatabase implements UserRepositoryPort {

//    private final UserRepositoryDynamoDB userRepositoryDynamoDB;
//
//    public UserRepositoryDatabase(UserRepositoryDynamoDB userRepositoryDynamoDB) {
//        this.userRepositoryDynamoDB = userRepositoryDynamoDB;
//    }


    @Override
    public Optional<User> get(String userId) {
        if(userId.equals("1")){
            return Optional.of(
                    new User(UUID.randomUUID(), "User1", "user1@gmail.com")
            );
        }
        return Optional.empty();
    }
}
