package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.ttl}")
    private int springRedisTtl;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private final ObjectMapper objectMapper;

    // Inject the ObjectMapper bean from JacksonConfig
    public RedisConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
//        this.objectMapper.registerModule(new JavaTimeModule()); // Ensure module is registered
    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        var connectionFactory = new LettuceConnectionFactory();
//        log.info("***** [LettuceConnectionFactory] - Starting REDIS with host: {} and port: {}", connectionFactory.getHostName(), connectionFactory.getPort());
//        return connectionFactory;
//    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        var config = new RedisStandaloneConfiguration(redisHost, redisPort);
        var connectionFactory = new LettuceConnectionFactory(config);
        log.info("***** [LettuceConnectionFactory] - Starting REDIS with host: {} and port: {}", connectionFactory.getHostName(), connectionFactory.getPort());
        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        log.info("***** [redisTemplate] - Starting REDIS with host: {} and port: {}", redisHost, redisPort);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
        log.info("***** [cacheManager] - Starting REDIS with host: {} and port: {}", redisHost, redisPort);
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(objectMapper))) // Explicitly use the configured ObjectMapper
                .entryTtl(Duration.ofMinutes(springRedisTtl));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

}