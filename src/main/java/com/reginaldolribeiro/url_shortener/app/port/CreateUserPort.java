package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserInput;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserOutput;

public interface CreateUserPort {
    CreateUserOutput save(CreateUserInput user);
}
