package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNullableException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Service
public class UrlSanitizer {

    public String sanitize(String url) {
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
