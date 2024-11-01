package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.Url;

import java.util.Optional;

public interface UrlRepositoryPort {
    Url save(Url url);
    Optional<Url> findByShortenedUrl(String shortenedUrl);
}
