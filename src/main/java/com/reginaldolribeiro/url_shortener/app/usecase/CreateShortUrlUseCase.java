package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.app.exception.UrlNullableException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class CreateShortUrlUseCase implements CreateShortUrlPort {

    private static final String BASE_URL = "https://short.url/";
    private final UserRepositoryPort userRepositoryPort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;

    public CreateShortUrlUseCase(UserRepositoryPort userRepositoryPort,
                                 UrlRepositoryPort urlRepositoryPort,
                                 UrlCacheRepositoryPort urlCacheRepositoryPort,
                                 IdGeneratorPort idGeneratorPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
        this.idGeneratorPort = idGeneratorPort;
    }

    @Override
    public CreateShortUrlOutput execute(CreateShortUrlInput input) {
        var sanitizedUrl = urlSanitizer(input.longUrl());
        var user = userRepositoryPort.get(input.userId())
                .orElseThrow(() -> new UserNotFoundException("User " + input.userId() + " not found."));
        return createShortUrl(input, user, sanitizedUrl);
    }

    private CreateShortUrlOutput createShortUrl(CreateShortUrlInput input, User user, String sanitizedUrl) {
        var shortenedUrl = generateShortUrl(input.longUrl());
        var url = Url.create(shortenedUrl, sanitizedUrl, user);
        urlRepositoryPort.save(url);
        urlCacheRepositoryPort.save(url);
        return new CreateShortUrlOutput(
                url.getUser().id().toString(),
                shortenedUrl,
                sanitizedUrl
        );
    }

    private String generateShortUrl(String longUrl) {
        var id = idGeneratorPort.generate();
//        return id;
        return BASE_URL + id;
    }

    private String urlSanitizer(String url) {
        final Pattern URL_SCHEME_PATTERN = Pattern.compile("^(http|https)://.*$");

        if (url == null || url.isBlank()) {
            throw new UrlNullableException("URL cannot be null or empty.");
        }

        if (!URL_SCHEME_PATTERN.matcher(url).matches()) {
            throw new InvalidUrlException("URL must be follow the patterns.");
        }

        // Parse the URL using URI to normalize it
        try {
            URI sanitizedUri = new URI(url);
            return sanitizedUri.toString();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException(e.getMessage(), e);
        }
    }

}
