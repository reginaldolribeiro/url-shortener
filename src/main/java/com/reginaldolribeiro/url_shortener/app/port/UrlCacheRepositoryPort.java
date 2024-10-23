package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.Url;

public interface UrlCacheRepositoryPort {
    void save(Url url);
    Url findByUrlId(String id);
}
