package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private final ObjectMapper cacheObjectMapper;

    public RedisConfig(@Qualifier("cacheObjectMapper") ObjectMapper cacheObjectMapper) {
        this.cacheObjectMapper = cacheObjectMapper;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(redisHost, redisPort);
        var connectionFactory = new LettuceConnectionFactory(config);
        log.info("***** [LettuceConnectionFactory] - Starting REDIS with host: {} and port: {}", connectionFactory.getHostName(), connectionFactory.getPort());
        return connectionFactory;
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(cacheObjectMapper)));
//                .entryTtl(Duration.ofMinutes(10));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

}