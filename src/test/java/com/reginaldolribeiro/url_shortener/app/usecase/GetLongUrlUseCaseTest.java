package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlDisabledException;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.ShortUrlMalformedException;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
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
    private final User USER = FixtureTests.createUser();
    private final String USER_ID = USER.id().toString();

    @InjectMocks
    private GetLongUrlUseCase getLongUrlUseCase;

    @Mock
    private UrlRepositoryPort urlRepositoryPort;
    @Mock
    private UrlCacheRepositoryPort urlCacheRepositoryPort;

    @Nested
    @DisplayName("Valid Short URL Codes")
    class ValidShortUrlCodes {

        @Test
        @DisplayName("Should find long URL in cache")
        public void shouldFindLongUrlInCache(){
            var expectedLongUrl = "https://example.com/very-long-url10";
            var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);

            when(urlCacheRepositoryPort.findByUrlId(SHORTENED_URL)).thenReturn(Optional.of(url));

            var longUrl = getLongUrlUseCase.execute(SHORTENED_URL);

            assertNotNull(longUrl);
            assertEquals(expectedLongUrl, longUrl);
            verify(urlCacheRepositoryPort, times(1)).findByUrlId(SHORTENED_URL);
            verify(urlRepositoryPort, times(0)).findByShortenedUrl(expectedLongUrl);
        }

        @Test
        @DisplayName("Should not find long URL in cache")
        public void shouldNotFindLongUrlInCache() {
            var expectedLongUrl = "https://example.com/very-long-url10";
            var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);

            when(urlCacheRepositoryPort.findByUrlId(SHORTENED_URL)).thenReturn(Optional.empty());
            when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

            var longUrl = getLongUrlUseCase.execute(SHORTENED_URL);

            assertNotNull(longUrl);
            assertEquals(expectedLongUrl, longUrl);
            verify(urlCacheRepositoryPort, times(1)).findByUrlId(SHORTENED_URL);
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
            verify(urlCacheRepositoryPort, times(0)).findByUrlId(SHORTENED_URL);
            verify(urlRepositoryPort, times(0)).findByShortenedUrl(invalidUrl);
        }

        @ParameterizedTest
        @ValueSource(strings = {"5vsR", "12345", "abc"}) // URLs with length less than 7
        @DisplayName("Should throw exception for URLs with invalid length")
        void shouldThrowExceptionForUrlsWithInvalidLength(String invalidUrl) {
            assertThrows(ShortUrlMalformedException.class, () -> getLongUrlUseCase.execute(invalidUrl));
            verify(urlCacheRepositoryPort, times(0)).findByUrlId(SHORTENED_URL);
            verify(urlRepositoryPort, times(0)).findByShortenedUrl(invalidUrl);
        }

    }

    @Test
    @DisplayName("Should throw exception when URL is disabled")
    public void shouldThrowExceptionWhenUrlIsDisabled() {
        var expectedLongUrl = "https://example.com/very-long-url10";
        var url = Url.create(SHORTENED_URL, expectedLongUrl, USER);
        url.disable();

        when(urlCacheRepositoryPort.findByUrlId(SHORTENED_URL)).thenReturn(Optional.of(url));
        when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

        assertThrows(UrlDisabledException.class, () -> getLongUrlUseCase.execute(SHORTENED_URL));

        verify(urlCacheRepositoryPort, times(1)).findByUrlId(SHORTENED_URL);
        verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
    }

}