package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlDisabledException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlNotFoundException;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.ShortUrlMalformedException;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.usecase.url.GetLongUrlUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLongUrlUseCaseTest {

    public static final String SHORTENED_URL = FixtureTests.SHORT_URL_CODE;
    private final User USER = FixtureTests.createSampleUser();
    private final String USER_ID = USER.getId().toString();

    @InjectMocks
    private GetLongUrlUseCase getLongUrlUseCase;

    @Mock
    private UrlRepositoryPort urlRepositoryPort;

    @Nested
    @DisplayName("Valid Short URL Codes")
    class ValidShortUrlCodes {

        @Test
        @DisplayName("Should find long URL from repository (cache or database)")
        public void shouldFindLongUrl() {
            var expectedLongUrl = "https://example.com/very-long-url10";
            var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);

            when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

            var longUrl = getLongUrlUseCase.execute(SHORTENED_URL);

            assertNotNull(longUrl);
            assertEquals(expectedLongUrl, longUrl);
            verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
        }

        @Test
        @DisplayName("Should throw UrlNotFoundException when URL is not found")
        public void shouldThrowUrlNotFoundExceptionWhenUrlNotFound() {
            when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.empty());

            var exception = assertThrows(UrlNotFoundException.class, () -> getLongUrlUseCase.execute(SHORTENED_URL));
            assertEquals("URL " + SHORTENED_URL + " not found.", exception.getMessage());

            verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
        }

        @Test
        @DisplayName("Should throw UrlDisabledException when URL is disabled")
        public void shouldThrowUrlDisabledExceptionWhenUrlIsDisabled() {
            var expectedLongUrl = "https://example.com/very-long-url10";
            var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);
            url.disable();

            when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

            var exception = assertThrows(UrlDisabledException.class, () -> getLongUrlUseCase.execute(SHORTENED_URL));
            assertEquals("URL is disabled.", exception.getMessage());

            verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
        }

    }

    @Nested
    @DisplayName("Invalid Short URL Codes")
    class InvalidShortUrlCodes {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "}) // Additional blank string test
        @DisplayName("Should throw exception for null, empty, or blank URL inputs")
        void shouldThrowExceptionForNullEmptyOrBlankUrlInputs(String invalidUrl) {
            assertThrows(ShortUrlMalformedException.class, () -> getLongUrlUseCase.execute(invalidUrl));
            verifyNoInteractions(urlRepositoryPort);
        }

        @ParameterizedTest
        @ValueSource(strings = {"5vsR", "12345", "abc"}) // URLs with length less than 7
        @DisplayName("Should throw exception for URLs with invalid length")
        void shouldThrowExceptionForUrlsWithInvalidLength(String invalidUrl) {
            assertThrows(ShortUrlMalformedException.class, () -> getLongUrlUseCase.execute(invalidUrl));
            verifyNoInteractions(urlRepositoryPort);
        }

    }

    @Test
    @DisplayName("Should throw exception when URL is disabled")
    public void shouldThrowExceptionWhenUrlIsDisabled() {
        var expectedLongUrl = "https://example.com/very-long-url10";
        var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);
        url.disable();

        when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

        UrlDisabledException exception = assertThrows(UrlDisabledException.class, () -> getLongUrlUseCase.execute(SHORTENED_URL));
        assertEquals("URL is disabled.", exception.getMessage());

        verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
    }

}