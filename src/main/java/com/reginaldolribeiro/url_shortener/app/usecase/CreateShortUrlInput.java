package com.reginaldolribeiro.url_shortener.app.usecase;

public record CreateShortUrlInput(String userId, String longUrl) {
}
