package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Deprecated after migration to RedisGlobalIdGenerator")
@ExtendWith(MockitoExtension.class)
class IdGeneratorTest {

    @InjectMocks
    private IdGenerator idGenerator;

    @Nested
    @DisplayName("Base62 ID Generation Tests")
    class Base62IdGenerationTests {

        /**
         * Tests if generate() produces a 7-character Base62 string.
         * ^: Starts at the beginning.
         * [0-9A-Za-z]: Contains only digits (0-9), uppercase letters (A-Z), or lowercase letters (a-z).
         * +: Has one or more of these characters.
         * $: Ends at the last character.
         */
        @Test
        @DisplayName("Should generate a 7-character Base62 ID")
        void shouldGenerateA7CharacterBase62Id() {
            final String BASE62_PATTERN_REGEX = "^[0-9A-Za-z]+$";

            var id = idGenerator.generate();
            assertNotNull(id);
            assertFalse(id.isBlank());
            assertEquals(FixtureTests.SHORT_URL_ID_LENGTH, id.length());
            assertTrue(id.matches(BASE62_PATTERN_REGEX), "ID should match Base62 format");
        }

        @Test
        @DisplayName("Should generate unique IDs")
        void shouldGenerateUniqueIds() {
            Set<String> ids = new HashSet<>();
            for (int i = 0; i < 1000; i++) {
                String id = idGenerator.generate();
                assertTrue(ids.add(id), "ID should be unique, but found duplicate: " + id);
            }
            assertEquals(1000, ids.size(), "The number of unique IDs generated should be 1000.");
        }

    }

    @Nested
    @DisplayName("Base62 Conversion Tests")
    class Base62ConversionTests {

        @ParameterizedTest
        @ValueSource(longs = {0L, 1L, 12345L, Long.MAX_VALUE})
        @DisplayName("Should handle positive values for Base62 conversion")
        void shouldHandlePositiveValuesForBase62Conversion(long value) {
            String base62 = IdGenerator.toBase62(value);
            assertNotNull(base62);
            assertFalse(base62.isEmpty());
        }

        @Test
        @DisplayName("Should convert zero value to '0' in Base62")
        void shouldConvertZeroToBase62() {
            String zeroBase62 = IdGenerator.toBase62(0L);
            assertEquals("0", zeroBase62, "Base62 representation of zero should be '0'");
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, -12345L, -Long.MAX_VALUE})
        @DisplayName("Should handle negative values for Base62 conversion")
        void shouldHandleNegativeValuesForBase62Conversion(long negativeValue) {
            String base62Negative = IdGenerator.toBase62(negativeValue);
            assertNotNull(base62Negative);
            assertFalse(base62Negative.isEmpty());
        }
    }

}