package com.reginaldolribeiro.url_shortener;

import com.reginaldolribeiro.url_shortener.adapter.controller.user.UserResponse;
import com.reginaldolribeiro.url_shortener.adapter.repository.url.UrlEntity;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.adapter.repository.user.UserEntity;
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

    public static String generateSampleUserId() {
        return UUID.randomUUID().toString();
    }

    public static Url createSampleUrl(){
        return Url.create(SHORT_URL_CODE, DEFAULT_LONG_URL, createSampleUser());
    }

    public static UrlEntity createSampleUrlEntity() {
        return new UrlEntity(
                SHORT_URL_CODE,
                DEFAULT_LONG_URL,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                0,
                true
        );
    }

    public static UrlEntity createSampleUrlEntity(String shortUrl) {
        return new UrlEntity(
                shortUrl,
                DEFAULT_LONG_URL,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                0,
                true
        );
    }

    public static User createSampleUser(){
        return User.create(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL);
    }

    public static User createSampleUser(String name, String email){
        return User.create(name, email);
    }

    public static CreateUserInput createSampleUserInput(){
        return new CreateUserInput(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL);
    }

    public static CreateUserOutput createSampleUserOutput(){
        return new CreateUserOutput(UUID.randomUUID(),
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true);
    }

    public static UserResponse userSampleResponse(UUID id){
        return new UserResponse(
                id,
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true
        );
    }

    public static UserResponse userSampleResponse(){
        return new UserResponse(
                UUID.randomUUID(),
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true
        );
    }

    public static UserEntity getUserEntity(){
        return new UserEntity(
                UUID.randomUUID().toString(),
                DEFAULT_USER_NAME,
                DEFAULT_USER_EMAIL,
                LocalDateTime.now(Clock.systemUTC()),
                LocalDateTime.now(Clock.systemUTC()),
                true
        );
    }

    public static String getCacheKey(String key) {
        return "urlCache::" + key;
    }

}
