package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.usecase.user.GetUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetUserUseCase Tests")
@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Nested
    @DisplayName("findById Success Cases")
    class FindByIdSuccess {

        @Test
        @DisplayName("Should return user when ID is found in repository")
        void shouldReturnUserWhenIdIsFound() {
            var user = FixtureTests.createUser();

            when(userRepositoryPort.findById(user.getId().toString())).thenReturn(Optional.of(user));

            var result = getUserUseCase.findById(user.getId());

            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(user.getId().getClass(), UUID.class),
                    () -> assertEquals(user.getId(), result.getId()),
                    () -> assertEquals(user.getName(), result.getName()),
                    () -> assertEquals(user.getEmail(), result.getEmail())
            );

            verify(userRepositoryPort, times(1)).findById(user.getId().toString());
        }
    }


    @Nested
    @DisplayName("findById Failure Cases")
    class FindByIdFailure {

        @Test
        @DisplayName("Should throw UserNotFoundException when ID is not found in repository")
        void shouldThrowUserNotFoundExceptionWhenIdIsNotFound() {
            var userId = UUID.randomUUID();

            when(userRepositoryPort.findById(userId.toString())).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> getUserUseCase.findById(userId));
            verify(userRepositoryPort, times(1)).findById(userId.toString());
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid-uuid", "", "1234"})
        @DisplayName("Should throw IllegalArgumentException for invalid UUID format")
        void shouldThrowIllegalArgumentExceptionForInvalidUUIDFormat(String invalidId) {
            assertThrows(IllegalArgumentException.class, () -> {
                getUserUseCase.findById(UUID.fromString(invalidId));
            });
        }
    }

}