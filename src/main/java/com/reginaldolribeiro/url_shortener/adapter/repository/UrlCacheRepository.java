package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlCacheRepository implements UrlCacheRepositoryPort {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public UrlCacheRepository(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(Url url) {
        log.info("Saving URL {} to cache ...", url.getId());

        var urlMappings = UrlMappings.create(url.getId(),
                url.getLongUrl(),
                url.getUser().id().toString());

        String cacheKey = "urlCache::" + urlMappings.getShortUrlId();

        redisTemplate.opsForValue().set(cacheKey, urlMappings);
    }

    @Override
    public Url findByUrlId(String id) {
        log.info("Searching for URL {} in cache ...", id);

        var cachedValue = redisTemplate.opsForValue().get(id);

        if(cachedValue == null){
            return null;
        }

        try{
            return objectMapper.convertValue(cachedValue, Url.class);
        } catch (IllegalArgumentException e){
            // Handle the case where the conversion fails
            System.err.println("Failed to convert cached value to Url: " + e.getMessage());
            return null;
        }

    }

}
