package com.reginaldolribeiro.url_shortener.app.usecase.url;

public record CreateShortUrlInput(String userId, String longUrl) {
}
