package com.reginaldolribeiro.url_shortener.app.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlTest {

    private static final String VALID_ID = "2cnbJVQ";
    private static final String VALID_LONG_URL = "https://example.com/very-long-url8";
    private static final User VALID_USER = new User(UUID.randomUUID(), "User1", "user1@gmail.com");

    @Nested
    @DisplayName("Valid URL creation tests")
    class ValidUrlCreationTests {

        @Test
        @DisplayName("Should create a URL with valid parameters")
        void shouldCreateAnUrlWithValidParameters() {
            var url = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);

            assertAll(
                    () -> assertNotNull(url),
                    () -> assertEquals(VALID_ID, url.getId()),
                    () -> assertEquals(VALID_LONG_URL, url.getLongUrl()),
                    () -> assertEquals("User1", url.getUser().name()),
                    () -> assertEquals("user1@gmail.com", url.getUser().email())
            );
        }

        @Test
        @DisplayName("Should set created date correctly")
        void shouldSetCreatedDateCorrectly() {
            Url url = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);
            assertAll(
                    () -> assertNotNull(url),
                    () -> assertNotNull(url.getCreatedDate()),
                    () -> assertTrue(url.getCreatedDate().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)))
            );
        }
    }

    @Nested
    @DisplayName("Invalid URL creation tests")
    class InvalidUrlCreationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void shouldNotCreateAnUrlWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Url.create(null, VALID_LONG_URL, VALID_USER));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is blank")
        void shouldNotCreateAnUrlWhenIdIsBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> Url.create("   ", VALID_LONG_URL, VALID_USER));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when longUrl is null")
        void shouldNotCreateAnUrlWhenLongUrlIsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> Url.create(VALID_ID, null, VALID_USER));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when longUrl is blank")
        void shouldNotCreateAnUrlWhenLongUrlIsBlank() {
            assertThrows(IllegalArgumentException.class,
                    () -> Url.create(VALID_ID, "   ", VALID_USER));
        }
    }

    @Nested
    @DisplayName("URL state management tests")
    class UrlStateManagementTests {

        @Test
        @DisplayName("Should enable the URL")
        void shouldEnableUrl() {
            Url url = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);
            url.disable(); // Ensure it's disabled first
            assertFalse(url.isActive());

            url.enable();
            assertTrue(url.isActive());
        }

        @Test
        @DisplayName("Should disable the URL")
        void shouldDisableUrl() {
            Url url = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);
            assertTrue(url.isActive());

            url.disable();
            assertFalse(url.isActive());
        }
    }

    @Nested
    @DisplayName("URL click tracking tests")
    class UrlClickTrackingTests {

        @Test
        @DisplayName("Should increment clicks correctly")
        void shouldIncrementClicksCorrectly() {
            Url url = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);
            int initialClicks = url.getClicks();

            url.incrementClick();
            assertEquals(initialClicks + 1, url.getClicks());
        }
    }

    @Nested
    @DisplayName("URL Builder tests")
    class UrlBuilderTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null in Builder")
        void shouldThrowExceptionWhenIdIsNullInBuilder() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Url.Builder()
                            .longUrl(VALID_LONG_URL)
                            .user(VALID_USER)
                            .clicks(0)
                            .isActive(true)
                            .build());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is blank in Builder")
        void shouldThrowExceptionWhenIdIsBlankInBuilder() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Url.Builder()
                            .id("   ")
                            .longUrl(VALID_LONG_URL)
                            .user(VALID_USER)
                            .clicks(0)
                            .isActive(true)
                            .build());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when longUrl is null in Builder")
        void shouldThrowExceptionWhenLongUrlIsNullInBuilder() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Url.Builder()
                            .id(VALID_ID)
                            .user(VALID_USER)
                            .clicks(0)
                            .isActive(true)
                            .build());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when longUrl is blank in Builder")
        void shouldThrowExceptionWhenLongUrlIsBlankInBuilder() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Url.Builder()
                            .id(VALID_ID)
                            .longUrl("   ")
                            .user(VALID_USER)
                            .clicks(0)
                            .isActive(true)
                            .build());
        }
    }
}