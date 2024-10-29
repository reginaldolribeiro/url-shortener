package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import io.lettuce.core.RedisCommandTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {

    public static final Url URL = FixtureTests.createSampleUrl();
    public static final UrlEntity URL_ENTITY = new UrlEntity(
            URL.getId(),
            URL.getLongUrl(),
            URL.getCreatedAt(),
            URL.getUpdatedAt(),
            URL.getUser().getId().toString(),
            URL.getClicks(),
            URL.isActive()
    );

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("save method")
    class SaveTests {

        @Test
        @DisplayName("Should save UrlEntity to Redis successfully")
        void shouldSaveUrlSuccessfully() {
            assertDoesNotThrow(() -> urlCacheRepository.save(URL));
            verify(redisTemplate.opsForValue(), times(1)).set(FixtureTests.getCacheKey(URL.getId()), URL_ENTITY);
        }

        @Test
        @DisplayName("should handle RedisConnectionFailureException during save")
        void handleRedisConnectionFailureException() {
            doThrow(RedisConnectionFailureException.class).when(valueOperations).set(anyString(), any());

            assertDoesNotThrow(() -> urlCacheRepository.save(URL));

            verify(valueOperations).set(anyString(), any());
        }

        @Test
        @DisplayName("should handle RedisCommandTimeoutException during save")
        void handleRedisCommandTimeoutException() {
            doThrow(RedisCommandTimeoutException.class).when(valueOperations).set(anyString(), any());

            assertDoesNotThrow(() -> urlCacheRepository.save(URL));

            verify(valueOperations).set(anyString(), any());
        }

        @Test
        @DisplayName("should handle IllegalArgumentException during save")
        void handleIllegalArgumentExceptionDuringSave() {
            doThrow(IllegalArgumentException.class).when(valueOperations).set(anyString(), any());

            assertDoesNotThrow(() -> urlCacheRepository.save(URL));

            verify(valueOperations).set(anyString(), any());
            verifyNoMoreInteractions(redisTemplate);
        }

        @Test
        @DisplayName("should handle unexpected RuntimeException during save")
        void handleUnexpectedRuntimeExceptionDuringSave() {
            doThrow(new RuntimeException("Unexpected error")).when(valueOperations).set(anyString(), any());

            assertDoesNotThrow(() -> urlCacheRepository.save(URL));

            verify(valueOperations).set(anyString(), any());
        }
    }

    @Nested
    @DisplayName("findByUrlId method")
    class FindByUrlIdMethodTests {

        @Test
        @DisplayName("should return empty optional if URL not found in cache")
        void returnEmptyIfNotFound() {
            when(valueOperations.get(anyString())).thenReturn(null);

            var invalidId = "invalidId";
            var result = assertDoesNotThrow(() -> urlCacheRepository.findByUrlId(invalidId));

            assertTrue(result.isEmpty());

            verify(redisTemplate.opsForValue(), times(1)).get(FixtureTests.getCacheKey(invalidId));
            verify(userRepositoryPort, times(0)).findById(invalidId);
        }

        @Test
        @DisplayName("should retrieve URL from cache and return mapped result")
        void retrieveUrlSuccessfully() {
            var user = URL.getUser();

            when(valueOperations.get(anyString())).thenReturn(URL_ENTITY);
            when(objectMapper.convertValue(any(), eq(UrlEntity.class))).thenReturn(URL_ENTITY);
            when(userRepositoryPort.findById(URL.getUser().getId().toString())).thenReturn(Optional.of(user));

            var result = urlCacheRepository.findByUrlId(URL_ENTITY.shortUrlId());

            assertTrue(result.isPresent());
            var returnedUrl = result.get();

            assertAll(
                    () -> assertEquals(URL_ENTITY.shortUrlId(), returnedUrl.getId()),
                    () -> assertEquals(URL_ENTITY.longUrl(), returnedUrl.getLongUrl()),
                    () -> assertTrue(URL_ENTITY.createdAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertTrue(URL_ENTITY.updatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertEquals(user, returnedUrl.getUser()),
                    () -> assertEquals(URL_ENTITY.clicks(), returnedUrl.getClicks()),
                    () -> assertTrue(returnedUrl.isActive())
            );

            verify(redisTemplate.opsForValue(), times(1)).get(FixtureTests.getCacheKey(URL_ENTITY.shortUrlId()));
            verify(userRepositoryPort, times(1)).findById(URL_ENTITY.userId());
        }

        @Test
        @DisplayName("should handle UserNotFoundException if user is missing in DB")
        void handleUserNotFoundException() {
            when(valueOperations.get(anyString())).thenReturn(URL_ENTITY);
            when(objectMapper.convertValue(any(), eq(UrlEntity.class))).thenReturn(URL_ENTITY);
            when(userRepositoryPort.findById(anyString())).thenReturn(Optional.empty());

            var result = assertDoesNotThrow(() -> urlCacheRepository.findByUrlId(URL_ENTITY.shortUrlId()));

            assertTrue(result.isEmpty());

            verify(redisTemplate.opsForValue(), times(1)).get(FixtureTests.getCacheKey(URL_ENTITY.shortUrlId()));
            verify(userRepositoryPort, times(1)).findById(URL_ENTITY.userId());
        }

        @ParameterizedTest
        @DisplayName("should handle exceptions gracefully when retrieving URL")
        @ValueSource(classes = {IllegalArgumentException.class, RedisConnectionFailureException.class})
        void handleExceptionsWhenRetrievingUrl(Class<Exception> exceptionClass) {
            when(valueOperations.get(anyString())).thenThrow(exceptionClass);

            var id = FixtureTests.SHORT_URL_CODE;
            var result = assertDoesNotThrow(() -> urlCacheRepository.findByUrlId(id));

            assertTrue(result.isEmpty());

            verify(redisTemplate.opsForValue(), times(1)).get(FixtureTests.getCacheKey(id));
            verify(userRepositoryPort, times(0)).findById(anyString());
        }
    }

}