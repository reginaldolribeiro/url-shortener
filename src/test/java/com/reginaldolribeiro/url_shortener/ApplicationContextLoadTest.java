package com.reginaldolribeiro.url_shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.app.port.CreateShortUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ApplicationContextLoadTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CreateShortUrlPort createShortUrlPort;

    @Autowired
    private GetLongUrlPort getLongUrlPort;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Test
    void contextLoads() {
        // Basic context load assertions
        assertNotNull(createShortUrlPort);
        assertNotNull(getLongUrlPort);
        assertNotNull(redisTemplate);
        assertNotNull(redisCacheManager);
        assertNotNull(objectMapper);
        assertNotNull(dynamoDbClient);
    }

    @Test
    void validateObjectMapperConfiguration() {
        assertTrue(
                objectMapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"),
                "JavaTimeModule should be registered with ObjectMapper"
        );
    }

    @Test
    void validateRedisTemplateConfiguration() {
        assertTrue(
                redisTemplate.getKeySerializer() instanceof org.springframework.data.redis.serializer.StringRedisSerializer,
                "RedisTemplate should use StringRedisSerializer for keys"
        );
        assertTrue(
                redisTemplate.getValueSerializer() instanceof org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer,
                "RedisTemplate should use GenericJackson2JsonRedisSerializer for values"
        );
    }

    @Test
    void createShortUrlPortShouldBeConfiguredCorrectly() {
        var bean = context.getBean("createShortUrlPort");

        // Verify that bean is not null and correct type
        assertNotNull(bean, "createShortUrlPort should be initialized");
        assertTrue(bean instanceof CreateShortUrlPort, "Bean should be of type CreateShortUrlPort");
        assertTrue(bean instanceof CreateShortUrlUseCase, "Bean should be an instance of CreateShortUrlUseCase");
    }

}