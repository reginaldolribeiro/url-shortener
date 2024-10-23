package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import org.springframework.stereotype.Service;


@Service
public class UrlRepositoryDatabase implements UrlRepositoryPort {

    private final UrlRepositoryDynamoDB urlRepositoryDynamoDB;

    public UrlRepositoryDatabase(UrlRepositoryDynamoDB urlRepositoryDynamoDB) {
        this.urlRepositoryDynamoDB = urlRepositoryDynamoDB;
    }

    @Override
    public void save(Url url) {
        System.out.println("Saving URL to database ....");
        var urlMappings = UrlMappings.create(url.getId(), url.getLongUrl(), url.getUser().id().toString());
        urlRepositoryDynamoDB.save(urlMappings);
    }

}