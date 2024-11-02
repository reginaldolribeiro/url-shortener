package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.adapter.repository.url.UrlEntity;
import com.reginaldolribeiro.url_shortener.app.domain.Url;

import java.util.Optional;

public interface UrlRepositoryPort {
    UrlEntity save(Url url);
    Optional<UrlEntity> findByShortenedUrl(String shortenedUrl);
}
