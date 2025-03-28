package com.reginaldolribeiro.url_shortener.adapter;

import com.reginaldolribeiro.url_shortener.app.port.IdGeneratorPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.util.List;

@Service
public class GlobalCounter implements IdGeneratorPort {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int SHORT_URL_ID_LENGTH = 7;
    private static final String REDIS_COUNTER_KEY = "shorturl:id:counter";

    private final StringRedisTemplate redisTemplate;
    private final Sqids sqids;

    public GlobalCounter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.sqids = Sqids.builder()
                .minLength(SHORT_URL_ID_LENGTH)
                .alphabet(BASE62)
                .build();
    }

    @Override
    public String generate() {
        Long id = redisTemplate.opsForValue().increment(REDIS_COUNTER_KEY);
        return sqids.encode(List.of(id));
    }

}
