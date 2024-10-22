package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UrlCacheRepository implements UrlCacheRepositoryPort {
    @Override
    public void save(Url url) {
        System.out.println("Saving URL to cache ...");
    }

    @Override
    public Url findByUrlId(String id) {
        System.out.println("Searching for URL in cache ...");
        return null;
    }
}
