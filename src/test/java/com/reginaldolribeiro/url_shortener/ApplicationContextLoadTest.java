package com.reginaldolribeiro.url_shortener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.reginaldolribeiro.url_shortener.app.port.CreateShortUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.CreateUserPort;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.GetUserPort;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlUseCase;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserUseCase;
import com.reginaldolribeiro.url_shortener.app.usecase.user.GetUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApplicationContextLoadTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private CreateShortUrlPort createShortUrlPort;

    @Autowired
    private GetLongUrlPort getLongUrlPort;

    @Autowired
    private CreateUserPort createUserPort;

    @Autowired
    private GetUserPort getUserPort;

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
        assertNotNull(createUserPort);
        assertNotNull(getUserPort);
        assertNotNull(redisTemplate);
        assertNotNull(redisCacheManager);
        assertNotNull(objectMapper);
        assertNotNull(dynamoDbClient);
    }

    @Test
    void validateObjectMapperConfiguration() {
        Set<Object> moduleIds = objectMapper.getRegisteredModuleIds();
        assertAll(
                () -> assertTrue(moduleIds.contains("com.fasterxml.jackson.datatype.jdk8.Jdk8Module")),
                () -> assertTrue(moduleIds.contains("jackson-datatype-jsr310")),
                () -> assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)),
                () -> assertFalse(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)),
                () -> assertEquals(PropertyNamingStrategies.SNAKE_CASE, objectMapper.getPropertyNamingStrategy()),
                () -> assertTrue(objectMapper.getDateFormat() instanceof StdDateFormat, "default DateFormat used by Jackson’s ObjectMapper")
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

    @Test
    void createUserPortShouldBeConfiguredCorrectly() {
        var bean = context.getBean("createUserPort");

        // Verify that bean is not null and correct type
        assertNotNull(bean, "createUserPort should be initialized");
        assertTrue(bean instanceof CreateUserPort);
        assertTrue(bean instanceof CreateUserUseCase);
    }

    @Test
    void getUserPortShouldBeConfiguredCorrectly() {
        var bean = context.getBean("getUserPort");

        assertNotNull(bean);
        assertTrue(bean instanceof GetUserPort);
        assertTrue(bean instanceof GetUserUseCase);
    }

}