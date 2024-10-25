package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.Url;

import java.util.Optional;

public interface UrlCacheRepositoryPort {
    void save(Url url);
    Optional<Url> findByUrlId(String id);
}
