package com.reginaldolribeiro.url_shortener.app.usecase.user;

import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.CreateUserPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;

public class CreateUserUseCase implements CreateUserPort{

    private final UserRepositoryPort userRepositoryPort;

    public CreateUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public CreateUserOutput save(CreateUserInput input) {
        var user = User.create(input.name(), input.email());
        userRepositoryPort.save(user);
        return new CreateUserOutput(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive());
    }

}
