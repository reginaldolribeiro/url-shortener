package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisGlobalIdGeneratorTest {

    @InjectMocks
    private RedisGlobalIdGenerator idGenerator;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        idGenerator = new RedisGlobalIdGenerator(redisTemplate);
    }

    @Test
    @DisplayName("Should generate a 7-character Base62 ID using GlobalCounter")
    void shouldGenerate7CharId() {
        final String BASE62_PATTERN_REGEX = "^[0-9A-Za-z]{7}$";

        when(valueOperations.increment("shorturl:id:counter")).thenReturn(12345L);
        String id = idGenerator.generate();

        assertNotNull(id);
        assertFalse(id.isBlank());
        assertEquals(7, id.length());
        assertTrue(id.matches(BASE62_PATTERN_REGEX), "ID should match Base62 format");
    }

    @Test
    @DisplayName("Should generate unique IDs using GlobalCounter")
    void shouldGenerateUniqueIds() {
        final String BASE62_PATTERN_REGEX = "^[0-9A-Za-z]{7}$";
        Set<String> ids = new HashSet<>();

        for (long i = 1; i <= 1000; i++) {
            when(valueOperations.increment("shorturl:id:counter")).thenReturn(i);
            String id = idGenerator.generate();
            assertTrue(id.matches(BASE62_PATTERN_REGEX), "ID should match Base62 format");
            assertTrue(ids.add(id), "Duplicate ID found: " + id);
        }

        assertEquals(1000, ids.size(), "The number of unique IDs generated should be 1000.");
    }

}