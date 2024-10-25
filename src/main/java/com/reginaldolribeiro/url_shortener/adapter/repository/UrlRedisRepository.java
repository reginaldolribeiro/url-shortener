package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
        redisTemplate.opsForValue().set(cacheKey, urlEntity);
    }

    public Optional<UrlEntity> findByUrlId(String id) {
        try{
            var cachedValue = objectMapper.convertValue(redisTemplate.opsForValue().get("urlCache::" + id),
                    UrlEntity.class);

            if(cachedValue == null){
                return Optional.empty();
            }

            return Optional.of(cachedValue);

        } catch (IllegalArgumentException e){
            // Handle the case where the conversion fails
            System.err.println("Failed to convert cached value to Url: " + e.getMessage());
            return Optional.empty();
        }
    }

}
