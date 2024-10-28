package com.reginaldolribeiro.url_shortener.app.usecase.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.GetUserPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;

import java.util.UUID;

public class GetUserUseCase implements GetUserPort {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User findById(UUID id) {
        return userRepositoryPort.findById(id.toString())
                .orElseThrow(() -> new UserNotFoundException("User " + id.toString() + " not found."));
    }

}
