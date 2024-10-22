package com.reginaldolribeiro.url_shortener.app.usecase;

public record CreateShortUrlOutput(String userId, String shortUrl, String longUrl) {
}
