package com.reginaldolribeiro.url_shortener.app.usecase.url;

public record CreateShortUrlOutput(String userId, String shortUrl, String longUrl) {
}
