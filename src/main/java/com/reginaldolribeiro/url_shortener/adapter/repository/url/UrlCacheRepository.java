package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@Slf4j
//@Service
public class UrlCacheRepository implements UrlCacheRepositoryPort {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepositoryPort userRepositoryPort;

    public UrlCacheRepository(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, UserRepositoryPort userRepositoryPort) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void save(Url url) {
        log.info("Saving URL {} to cache ...", url.getId());
        var urlEntity = UrlMapper.toEntity(url);
        var cacheKey = "urlCache::" + urlEntity.getShortUrlId();
        try {
            redisTemplate.opsForValue().set(cacheKey, urlEntity);
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            log.warn("Redis connection issue while saving URL {}: {}", urlEntity.getShortUrlId(), e.getMessage());
            // Optionally retry or fallback
        } catch (IllegalArgumentException e) {
            log.error("Serialization error for URL {}: {}", urlEntity.getShortUrlId(), e.getMessage());
            // Handle serialization issues
        } catch (Exception e) {
            log.error("Unexpected error while saving URL to cache", e);
        }
    }

    @Override
    public Optional<Url> findByUrlId(String id) {
        log.info("Searching for URL {} in cache ...", id);
        try {
            var cachedValue = objectMapper.convertValue(
                    redisTemplate.opsForValue().get("urlCache::" + id),
                    UrlEntity.class
            );

            if (cachedValue == null) {
                return Optional.empty();
            }

            var user = userRepositoryPort.findById(cachedValue.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User " + cachedValue.getUserId() + " not found."));

            return Optional.of(UrlMapper.toDomain(cachedValue, user));

        } catch (IllegalArgumentException e) {
            log.warn("Failed to convert cached value to UrlEntity for ID {}: {}", id, e.getMessage());
        } catch (RedisConnectionFailureException e) {
            log.warn("Failed to retrieve URL from cache for ID {}: {}", id, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while retrieving URL from cache for ID {}", id, e);
        }

        return Optional.empty();
    }

}
