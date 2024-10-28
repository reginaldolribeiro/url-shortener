package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserInput;
import com.reginaldolribeiro.url_shortener.app.usecase.user.CreateUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Nested
    @DisplayName("Successful User Creation")
    class SuccessfulUserCreationTests {

        @Test
        @DisplayName("Should save user successfully with valid input")
        void shouldSaveUser() {
            var userInput = FixtureTests.createUserInput();
            var expectedUser = User.create(userInput.name(), userInput.email());

            doNothing().when(userRepositoryPort).save(any(User.class));

            var userOutput = createUserUseCase.save(userInput);

            assertAll(
                    () -> assertNotNull(userOutput),
                    () -> assertNotNull(userOutput.id()),
                    () -> assertEquals(userOutput.id().getClass(), UUID.class),
                    () -> assertEquals(userInput.name(), userOutput.name()),
                    () -> assertEquals(userInput.email(), userOutput.email()),
                    () -> assertTrue(userOutput.createdAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertTrue(userOutput.updatedAt().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1))),
                    () -> assertTrue(userOutput.active())
            );

            verify(userRepositoryPort, times(1)).save(any(User.class));
        }

    }


    @Nested
    @DisplayName("Invalid User Creation Input")
    class InvalidUserCreationInputTests {

        @ParameterizedTest
        @CsvSource({
                ", ",                   // null name and null email
                "'', ''",               // empty name and empty email
                "' ', ' '",             // blank name and blank email
                ", user@user.com",      // null name, valid email
                "User1, ",              // valid name, null email
                "'', user@user.com",    // empty name, valid email
                "User1, ''",            // valid name, empty email
                "' ', user@user.com",   // blank name, valid email
                "User1, ' '"            // valid name, blank email
        })
        @DisplayName("Should throw exception for null, empty, or blank inputs")
        void shouldNotCreateUser(String name, String email) {
            var input = new CreateUserInput(name, email);
            assertThrows(IllegalArgumentException.class, () -> createUserUseCase.save(input));
            verify(userRepositoryPort, times(0)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when repository.save fails")
        void shouldThrowExceptionWhenRepositorySaveFails() {
            var userInput = FixtureTests.createUserInput();

            doThrow(new RuntimeException("Database error")).when(userRepositoryPort).save(any(User.class));

            assertThrows(RuntimeException.class, () -> createUserUseCase.save(userInput), "Database error");
            verify(userRepositoryPort, times(1)).save(any(User.class));
        }

    }

}