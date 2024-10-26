package com.reginaldolribeiro.url_shortener;

import com.reginaldolribeiro.url_shortener.app.domain.User;

import java.util.UUID;

public class FixtureTests {

    public static final int SHORT_URL_ID_LENGTH = 7;
    public static final String SHORT_URL_CODE = "2cnbJVQ";
    public static final String DEFAULT_LONG_URL = "https://example.com/long-url";
    public static final String DEFAULT_DOMAIN = "https://short.url/";

    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    public static User createUser(){
        return new User(UUID.randomUUID(), "User1", "user@user.com");
    }

    public static User createUser(String name, String email){
        return new User(UUID.randomUUID(), name, email);
    }

    public static User createUser(UUID id, String name, String email){
        return new User(id, name, email);
    }

}
