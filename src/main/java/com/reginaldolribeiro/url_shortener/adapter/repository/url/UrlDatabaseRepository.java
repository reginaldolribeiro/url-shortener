package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class UrlDatabaseRepository implements UrlRepositoryPort {

    private final UrlDynamoDBRepository urlDynamoDBRepository;

    public UrlDatabaseRepository(UrlDynamoDBRepository urlDynamoDBRepository) {
        this.urlDynamoDBRepository = urlDynamoDBRepository;
    }

    @Override
    public void save(Url url) {
        log.info("Saving URL to database ....");
        var urlEntity = new UrlEntity(url.getId(),
                url.getLongUrl(),
                url.getCreatedAt(),
                url.getUpdatedAt(),
                url.getUser().getId().toString(),
                url.getClicks(),
                url.isActive());
        urlDynamoDBRepository.save(urlEntity);
    }

    @Override
//    @Cacheable(value = "urlCache", key = "'urlCache::' + #shortenedUrl")
    public Optional<Url> findByShortenedUrl(String shortenedUrl) {
        log.info("Searching for {} in the database", shortenedUrl);
        return urlDynamoDBRepository.findByShortenedUrl(shortenedUrl);
    }

}