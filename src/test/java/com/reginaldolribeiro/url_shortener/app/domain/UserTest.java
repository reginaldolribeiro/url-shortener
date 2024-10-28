package com.reginaldolribeiro.url_shortener.app.domain;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Nested
    @DisplayName("Valid User creation tests")
    class ValidUserCreationTests {

        @Test
        @DisplayName("Should create a User with valid parameters")
        void shouldCreateAnUserWithValidParameters() {
            var user = FixtureTests.createUser();
            assertAll(
                    () -> assertNotNull(user),
                    () -> assertNotNull(user.getId()),
                    () -> assertEquals(user.getId().getClass(), UUID.class),
                    () -> assertEquals(user.getName(), FixtureTests.DEFAULT_USER_NAME),
                    () -> assertEquals(user.getEmail(), FixtureTests.DEFAULT_USER_EMAIL),
                    () -> assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertTrue(user.getUpdatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertTrue(user.isActive())
            );
        }

    }


    @Nested
    @DisplayName("Invalid User creation tests")
    class InvalidUserCreationTests{

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException when name is null, empty or blank")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void shouldNotCreateAnUserWhenNameIsNullOrEmptyOrBlank(String name) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.create(name, "user@user.com"));
        }

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException when email is null, empty or blank")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void shouldNotCreateAnUserWhenEmailIsNullOrEmptyOrBlank(String email) {
            assertThrows(IllegalArgumentException.class,
                    () -> User.create("User1", email));
        }

    }

    @Nested
    @DisplayName("URL Builder tests")
    class UrlBuilderTests {

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException when Name is null, empty or blank in Builder")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void shouldThrowExceptionWhenNameIsNullOrEmptyOrBlankInBuilder(String name) {
            assertThrows(IllegalArgumentException.class,
                    () -> new User.Builder()
                            .id(UUID.randomUUID())
                            .name(name)
                            .email(FixtureTests.DEFAULT_USER_EMAIL)
                            .createdAt(LocalDateTime.now(Clock.systemUTC()))
                            .updatedAt(LocalDateTime.now(Clock.systemUTC()))
                            .active(true)
                            .build());
        }

        @ParameterizedTest
        @DisplayName("Should throw IllegalArgumentException when Email is null, empty or blank in Builder")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        void shouldThrowExceptionWhenEmailIsNullOrEmptyOrBlankInBuilder(String email) {
            assertThrows(IllegalArgumentException.class,
                    () -> new User.Builder()
                            .id(UUID.randomUUID())
                            .name(FixtureTests.DEFAULT_USER_NAME)
                            .email(email)
                            .createdAt(LocalDateTime.now(Clock.systemUTC()))
                            .updatedAt(LocalDateTime.now(Clock.systemUTC()))
                            .active(true)
                            .build());
        }

    }

}