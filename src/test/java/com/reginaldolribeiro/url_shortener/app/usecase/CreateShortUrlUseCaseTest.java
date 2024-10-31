package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.IdGeneratorPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlInput;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlUseCase;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateShortUrlUseCaseTest {

    private final User USER = FixtureTests.createSampleUser();
    private final String USER_ID = USER.getId().toString();

    @InjectMocks
    private CreateShortUrlUseCase createShortUrlUseCase;

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private UrlRepositoryPort urlRepositoryPort;
    @Mock
    private UrlCacheRepositoryPort urlCacheRepositoryPort;
    @Mock
    private IdGeneratorPort idGeneratorPort;


    @Nested
    @DisplayName("Should create Short URL")
    class shouldCreateShortUrl {

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/long-url",
                "https://example.com/path%20with%20space",
                "https://example.com/query?name=John%20Doe",
                "https://example.com/resource#section",
                "https://example.com:8080/path",
        })
        @DisplayName("Should create short URL successfully with various valid URLs")
        public void testCreateShortUrlSuccessful(String longUrl) {
            var shortUrlCode = "xUk340p";
            var input = new CreateShortUrlInput(USER_ID, longUrl);

            when(userRepositoryPort.findById(USER_ID)).thenReturn(Optional.of(USER));
            when(idGeneratorPort.generate()).thenReturn(shortUrlCode);

            doNothing().when(urlRepositoryPort).save(any(Url.class));
            doNothing().when(urlCacheRepositoryPort).save(any(Url.class));

            var output = createShortUrlUseCase.execute(input);

            assertNotNull(output);
            assertEquals(USER_ID, output.userId());
            assertNotNull(output.shortUrl());

            assertEquals(shortUrlCode, output.shortUrl());
            assertEquals(FixtureTests.SHORT_URL_ID_LENGTH, shortUrlCode.length());

            assertEquals(longUrl, output.longUrl());
            verify(userRepositoryPort, times(1)).findById(USER_ID);
            verify(idGeneratorPort, times(1)).generate();
            verify(urlRepositoryPort, times(1)).save(any(Url.class));
            verify(urlCacheRepositoryPort, times(1)).save(any(Url.class));
        }

        @Test
        @DisplayName("Should return different Shortened URLs for the same Long URL with different user IDs")
        void shouldReturnDifferentShortenedUrlsForSameLongUrlAndDifferentUserIds() {
            var user1 = USER;
            var user2 = FixtureTests.createSampleUser("User2", "user2@user.com");
            var expectedShortUrl1 = "xUk340p";
            var expectedShortUrl2 = "aBcD123";
            var userId1 = user1.getId().toString();
            var userId2 = user2.getId().toString();

            var input1 = new CreateShortUrlInput(userId1, FixtureTests.DEFAULT_LONG_URL);
            var input2 = new CreateShortUrlInput(userId2, FixtureTests.DEFAULT_LONG_URL);

            // Set up mocks for the first user and URL
            when(userRepositoryPort.findById(userId1)).thenReturn(Optional.of(user1));
            when(idGeneratorPort.generate()).thenReturn(expectedShortUrl1);
            doNothing().when(urlRepositoryPort).save(any(Url.class));
            doNothing().when(urlCacheRepositoryPort).save(any(Url.class));

            // Execute the first creation
            var output1 = createShortUrlUseCase.execute(input1);

            // Set up mocks for the second user and URL (return a different shortened URL)
            when(userRepositoryPort.findById(userId2)).thenReturn(Optional.of(user2));
            when(idGeneratorPort.generate()).thenReturn(expectedShortUrl2);

            // Execute the second creation
            var output2 = createShortUrlUseCase.execute(input2);

            // Assertions
            assertNotNull(output1);
            assertNotNull(output2);
            assertNotEquals(output1.shortUrl(), output2.shortUrl(), "Shortened URLs should be different for different user IDs");
            assertEquals(expectedShortUrl1, output1.shortUrl());
            assertEquals(expectedShortUrl2, output2.shortUrl());

            // Verify that each user and URL pair was handled independently
            verify(userRepositoryPort, times(1)).findById(userId1);
            verify(userRepositoryPort, times(1)).findById(userId2);
            verify(idGeneratorPort, times(2)).generate();  // ID generation called separately for each request
            verify(urlRepositoryPort, times(2)).save(any(Url.class));
            verify(urlCacheRepositoryPort, times(2)).save(any(Url.class));
        }

    }


    @Nested
    @DisplayName("Should NOT create Short URL")
    class shouldNotCreateShortUrl {

        @Test
        @DisplayName("Should not create short URL when user is null or not found")
        public void testDoNotCreateShortUrlWithNullableUser() {
            String nullableUserId = null;
            var input = new CreateShortUrlInput(nullableUserId, FixtureTests.DEFAULT_LONG_URL);

            when(userRepositoryPort.findById(nullableUserId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> createShortUrlUseCase.execute(input));
            verify(userRepositoryPort, times(1)).findById(nullableUserId);
            verifyNoInteractions(idGeneratorPort);
            verifyNoInteractions(urlRepositoryPort);
            verifyNoInteractions(urlCacheRepositoryPort);
        }

        @Test
        @DisplayName("Should not create short URL with an invalid user ID")
        public void testDoNotCreateShortUrlWithAnInvalidUser() {
            var invalidUserId = "123";
            var input = new CreateShortUrlInput(invalidUserId, FixtureTests.DEFAULT_LONG_URL);

            when(userRepositoryPort.findById(invalidUserId)).thenThrow(UserNotFoundException.class);

            assertThrows(UserNotFoundException.class, () -> createShortUrlUseCase.execute(input));
            verify(userRepositoryPort, times(1)).findById(invalidUserId);
            verifyNoInteractions(idGeneratorPort);
            verifyNoInteractions(urlRepositoryPort);
            verifyNoInteractions(urlCacheRepositoryPort);
        }

        @Test
        @DisplayName("Should not create short URL when ID generation fails")
        public void testDoNotCreateShortUrlWhenHaveIdGenerationProblem() {
            var input = new CreateShortUrlInput(USER_ID, FixtureTests.DEFAULT_LONG_URL);

            when(userRepositoryPort.findById(USER_ID)).thenReturn(Optional.of(USER));

            when(idGeneratorPort.generate()).thenThrow(IdGenerationException.class);

            assertThrows(IdGenerationException.class, () -> createShortUrlUseCase.execute(input));
            verify(userRepositoryPort, times(1)).findById(USER_ID);
            verify(idGeneratorPort, times(1)).generate();
            verifyNoInteractions(urlRepositoryPort);
            verifyNoInteractions(urlCacheRepositoryPort);
        }

    }

}