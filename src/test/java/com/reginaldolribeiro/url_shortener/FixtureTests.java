package com.reginaldolribeiro.url_shortener;

import com.reginaldolribeiro.url_shortener.adapter.controller.user.UserResponse;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserInput;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserOutput;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

public class FixtureTests {

    public static final int SHORT_URL_ID_LENGTH = 7;
    public static final String SHORT_URL_CODE = "2cnbJVQ";
    public static final String DEFAULT_LONG_URL = "https://example.com/long-url";
    public static final String DEFAULT_DOMAIN = "https://short.url/";
    public static final String DEFAULT_USER_NAME = "User1";
    public static final String DEFAULT_USER_EMAIL = "user@user.com";

    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    public static User createUser(){
        return User.create(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL);
    }

    public static User createUser(String name, String email){
        return User.create(name, email);
    }

    public static CreateUserInput createUserInput(){
        return new CreateUserInput(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL);
    }

    public static CreateUserOutput createUserOutput(){
        return new CreateUserOutput(UUID.randomUUID(),
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true);
    }

    public static UserResponse userResponse(UUID id){
        return new UserResponse(
                id,
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true
        );
    }

    public static UserResponse userResponse(){
        return new UserResponse(
                UUID.randomUUID(),
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true
        );
    }

}
