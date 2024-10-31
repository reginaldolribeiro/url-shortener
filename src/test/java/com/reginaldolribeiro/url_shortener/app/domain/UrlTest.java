package com.reginaldolribeiro.url_shortener.app.domain;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlTest {

    private static final String VALID_ID = "2cnbJVQ";
    private static final String VALID_LONG_URL = "https://example.com/very-long-url8";
    private static final User VALID_USER = FixtureTests.createSampleUser();
    private static final Url URL = Url.create(VALID_ID, VALID_LONG_URL, VALID_USER);

    @Nested
    @DisplayName("Valid URL creation tests")
    class ValidUrlCreationTests {

        @Test
        @DisplayName("Should create a URL with valid parameters")
        void shouldCreateAnUrlWithValidParameters() {
            assertAll(
                    () -> assertNotNull(URL),
                    () -> assertEquals(VALID_ID, URL.getId()),
                    () -> assertEquals(VALID_LONG_URL, URL.getLongUrl()),
                    () -> assertEquals(VALID_USER.getName(), URL.getUser().getName()),
                    () -> assertEquals(VALID_USER.getEmail(), URL.getUser().getEmail())
            );
        }

        @Test
        @DisplayName("Should set created date correctly")
        void shouldSetcreatedAtCorrectly() {
            assertAll(
                    () -> assertNotNull(URL),
                    () -> assertNotNull(URL.getCreatedAt()),
                    () -> assertTrue(URL.getCreatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)))
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
            URL.disable(); // Ensure it's disabled first
            assertFalse(URL.isActive());

            URL.enable();
            assertTrue(URL.isActive());
        }

        @Test
        @DisplayName("Should disable the URL")
        void shouldDisableUrl() {
            assertTrue(URL.isActive());

            URL.disable();
            assertFalse(URL.isActive());
        }
    }

    @Nested
    @DisplayName("URL click tracking tests")
    class UrlClickTrackingTests {

        @Test
        @DisplayName("Should increment clicks correctly")
        void shouldIncrementClicksCorrectly() {
            int initialClicks = URL.getClicks();

            URL.incrementClick();
            assertEquals(initialClicks + 1, URL.getClicks());
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
                            .active(true)
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
                            .active(true)
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
                            .active(true)
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
                            .active(true)
                            .build());
        }
    }
}