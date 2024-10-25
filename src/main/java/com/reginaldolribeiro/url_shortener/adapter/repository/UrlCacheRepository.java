package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UrlCacheRepository implements UrlCacheRepositoryPort {

    private final UrlRedisRepository urlRedisRepository;
    private final UserRepositoryPort userRepositoryPort;

    public UrlCacheRepository(UrlRedisRepository urlRedisRepository, UserRepositoryPort userRepositoryPort) {
        this.urlRedisRepository = urlRedisRepository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void save(Url url) {
        log.info("Saving URL {} to cache ...", url.getId());

        var urlEntity = new UrlEntity(url.getId(),
                url.getLongUrl(),
                url.getCreatedDate(),
                url.getUser().id().toString(),
                url.getClicks(),
                url.isActive());

        urlRedisRepository.save(urlEntity);
    }

    @Override
    public Optional<Url> findByUrlId(String id) {
        log.info("Searching for URL {} in cache ...", id);

        return urlRedisRepository
                .findByUrlId(id)
                .map(cachedValue -> {
                    var user = userRepositoryPort.get("9b8a2db5-e50a-481f-9e9a-828c37e721c1")
                            .orElseThrow(() -> new UserNotFoundException("User " + cachedValue.userId() + " not found."));
                    return UrlEntity.fromMapping(
                            cachedValue.shortUrlId(),
                            cachedValue.longUrl(),
                            cachedValue.createdDate(),
                            user,
                            cachedValue.clicks(),
                            cachedValue.isActive()
                    );
                });
    }

}
