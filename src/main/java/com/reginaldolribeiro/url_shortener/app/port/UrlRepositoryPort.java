package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.domain.Url;

public interface UrlRepositoryPort {
    void save(Url url);
}
