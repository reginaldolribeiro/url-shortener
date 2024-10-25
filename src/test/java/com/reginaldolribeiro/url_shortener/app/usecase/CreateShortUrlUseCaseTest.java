package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.IdGeneratorPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateShortUrlUseCaseTest {

    private final String USER_ID = UUID.randomUUID().toString();
    private final String LONG_URL = "http://example.com/long-url";

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


    @Test
    public void testCreateShortUrlSuccessful() {
        final var SHORT_URL_ID_LENGTH = 7;
        final var SHORT_URL_CODE = "xUk340p";

        var input = new CreateShortUrlInput(USER_ID, LONG_URL);
        var user = new User(UUID.fromString(USER_ID), "Reginaldo Ribeiro", "reginaldo@gmail.com");

        when(userRepositoryPort.get(USER_ID)).thenReturn(Optional.of(user));
        when(idGeneratorPort.generate()).thenReturn(SHORT_URL_CODE);

//        var url = Url.create("abc345k", longUrl, user);
        doNothing().when(urlRepositoryPort).save(any(Url.class));
        doNothing().when(urlCacheRepositoryPort).save(any(Url.class));

        var output = createShortUrlUseCase.execute(input);

        assertNotNull(output);
        assertEquals(USER_ID, output.userId());
        assertNotNull(output.shortUrl());

        assertEquals(SHORT_URL_CODE, output.shortUrl());
        assertEquals(SHORT_URL_ID_LENGTH, SHORT_URL_CODE.length());

        assertEquals(LONG_URL, output.longUrl());
        verify(userRepositoryPort, times(1)).get(USER_ID);
        verify(idGeneratorPort, times(1)).generate();
        verify(urlRepositoryPort, times(1)).save(any(Url.class));
        verify(urlCacheRepositoryPort, times(1)).save(any(Url.class));
    }

    @Test
    public void testDoNotCreateShortUrlWithAnNullableUser() {
        String nullableUserId = null;
        String longUrl = "https://example.com/long-url";
        var input = new CreateShortUrlInput(nullableUserId, longUrl);

        when(userRepositoryPort.get(nullableUserId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> createShortUrlUseCase.execute(input));
        verify(userRepositoryPort, times(1)).get(nullableUserId);
        verify(idGeneratorPort, times(0)).generate();
        verify(urlRepositoryPort, times(0)).save(any(Url.class));
        verify(urlCacheRepositoryPort, times(0)).save(any(Url.class));
    }

    @Test
    public void testDoNotCreateShortUrlWithAnInvalidUser() {
        String invalidUserId = "123";
        String longUrl = "https://example.com/long-url";
        var input = new CreateShortUrlInput(invalidUserId, longUrl);

        when(userRepositoryPort.get(invalidUserId)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> createShortUrlUseCase.execute(input));
        verify(userRepositoryPort, times(1)).get(invalidUserId);
        verify(idGeneratorPort, times(0)).generate();
        verify(urlRepositoryPort, times(0)).save(any(Url.class));
        verify(urlCacheRepositoryPort, times(0)).save(any(Url.class));
    }

    @Test
    public void testDoNotCreateShortUrlWhenHaveIdGenerationProblem() {
        var input = new CreateShortUrlInput(USER_ID, LONG_URL);

        var user = new User(UUID.fromString(USER_ID), "Reginaldo Ribeiro", "reginaldo@gmail.com");
        when(userRepositoryPort.get(USER_ID)).thenReturn(Optional.of(user));

        when(idGeneratorPort.generate()).thenThrow(IdGenerationException.class);

        assertThrows(IdGenerationException.class, () -> createShortUrlUseCase.execute(input));
        verify(userRepositoryPort, times(1)).get(USER_ID);
        verify(idGeneratorPort, times(1)).generate();
        verify(urlRepositoryPort, times(0)).save(any(Url.class));
        verify(urlCacheRepositoryPort, times(0)).save(any(Url.class));
    }

}