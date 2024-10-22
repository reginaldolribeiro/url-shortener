package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UrlRepositoryDatabase implements UrlRepositoryPort {
    @Override
    public void save(Url url) {
        System.out.println("Saving URL to database ....");
    }
}
