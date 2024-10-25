package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNotFoundException;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLongUrlUseCaseTest {

    public static final String SHORTENED_URL = "5vsRJSy";

    @InjectMocks
    private GetLongUrlUseCase getLongUrlUseCase;

    @Mock
    private UrlRepositoryPort urlRepositoryPort;
    @Mock
    private UrlCacheRepositoryPort urlCacheRepositoryPort;

    @Test
    public void testShouldFindLongUrlInCache(){
        var expectedLongUrl = "https://example.com/very-long-url10";
        var user = new User(UUID.randomUUID(), "Reginaldo", "reginaldolribeiro@gmail.com");
        var url = Url.create(SHORTENED_URL, expectedLongUrl, user);

        when(urlCacheRepositoryPort.findByUrlId(SHORTENED_URL)).thenReturn(Optional.of(url));

        var longUrl = getLongUrlUseCase.execute(SHORTENED_URL);

        assertNotNull(longUrl);
        assertEquals(expectedLongUrl, longUrl);
        verify(urlCacheRepositoryPort, times(1)).findByUrlId(SHORTENED_URL);
        verify(urlRepositoryPort, times(0)).findByShortenedUrl(expectedLongUrl);
    }

    @Test
    public void testShouldNotFindLongUrlInCache(){
        var expectedLongUrl = "https://example.com/very-long-url10";
        var user = new User(UUID.randomUUID(), "Reginaldo", "reginaldolribeiro@gmail.com");
        var url = Url.create(SHORTENED_URL, expectedLongUrl, user);

        when(urlCacheRepositoryPort.findByUrlId(SHORTENED_URL)).thenReturn(Optional.empty());
        when(urlRepositoryPort.findByShortenedUrl(SHORTENED_URL)).thenReturn(Optional.of(url));

        var longUrl = getLongUrlUseCase.execute(SHORTENED_URL);

        assertNotNull(longUrl);
        assertEquals(expectedLongUrl, longUrl);
        verify(urlCacheRepositoryPort, times(1)).findByUrlId(SHORTENED_URL);
        verify(urlRepositoryPort, times(1)).findByShortenedUrl(SHORTENED_URL);
    }

    @Test
    public void testShouldThrowExceptionWhenUrlIsNull(){
        when(urlRepositoryPort.findByShortenedUrl(null)).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> getLongUrlUseCase.execute(null));
    }

    @Test
    public void testShouldThrowExceptionWhenUrlIsEmpty(){
        var emptyUrl = "";
        when(urlRepositoryPort.findByShortenedUrl(emptyUrl)).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> getLongUrlUseCase.execute(emptyUrl));
    }

    @Test
    public void testShouldThrowExceptionWhenUrlNotFound(){
        when(urlRepositoryPort.findByShortenedUrl(anyString())).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> getLongUrlUseCase.execute(anyString()));
    }

}