package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UrlRedisRepository {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public UrlRedisRepository(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    public void save(UrlEntity urlEntity) {
        var cacheKey = "urlCache::" + urlEntity.shortUrlId();
        try {
            redisTemplate.opsForValue().set(cacheKey, urlEntity);
        } catch (RedisConnectionFailureException e) {
            log.warn("Failed to save URL to cache: {}", e.getMessage());
            // Optionally, you can implement retry logic or metrics here
        } catch (Exception e) {
            log.error("Unexpected error while saving URL to cache", e);
        }
    }

    public Optional<UrlEntity> findByUrlId(String id) {
        try {
            var cachedValue = objectMapper.convertValue(
                    redisTemplate.opsForValue().get("urlCache::" + id),
                    UrlEntity.class
            );

            if (cachedValue == null) {
                return Optional.empty();
            }

            return Optional.of(cachedValue);

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
