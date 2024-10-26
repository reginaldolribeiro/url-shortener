package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNullableException;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Service
public class UrlSanitizer {

    // Apache UrlValidator for basic URL structure validation
    private static final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

    // Regex pattern to validate URL paths, allowing only alphanumeric characters, underscores, slashes, hyphens, and percent-encoded sequences.
    private static final Pattern PATH_VALIDATION_REGEX = Pattern.compile("^[a-zA-Z0-9/_%-]+$");

    public String sanitize(String url) {
        if (url == null || url.isBlank()) {
            throw new UrlNullableException("URL cannot be null or empty.");
        }

        // Step 1: Validate general URL structure and scheme with Apache UrlValidator
        if (!urlValidator.isValid(url)) {
            throw new InvalidUrlException("URL must be valid and start with http or https.");
        }

        try{
            // Step 2: Parse with URI to ensure a well-formed structure and valid host
            URI sanitizedUri = new URI(url);
            if (sanitizedUri.getHost() == null || sanitizedUri.getHost().isBlank()) {
                throw new InvalidUrlException("URL must contain a valid domain.");
            }

//            // Step 3: Additional check on the path for unwanted characters
//            String path = sanitizedUri.getPath();
//            if(path != null && !path.isBlank() && !path.matches(PATH_VALIDATION_REGEX.pattern())){
//                throw new InvalidUrlException("URL path contains invalid characters.");
//            }

            // Step 3: Validate the encoded path without decoding
            String path = extractPathFromUrl(url);
            if (path != null && !path.isBlank() && !path.matches(PATH_VALIDATION_REGEX.pattern())) {
                throw new InvalidUrlException("URL path contains invalid characters.");
            }

            return sanitizedUri.toString();
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL is malformed", e);
        }

    }

    // Helper method to extract the path from the URL string without decoding
    private String extractPathFromUrl(String url) {
        try {
            // Create a URI instance to access URL components
            URI uri = new URI(url);

            // Get the raw path, which retains encoding (like %20 for spaces)
            return uri.getRawPath(); // This method returns the path without decoding
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL is malformed", e);
        }
    }

    public boolean isValidShortUrlCode(String shortUrl) {
        var SHORT_URL_ID_LENGTH = 7;
        var BASE62_PATTERN_REGEX = "^[0-9A-Za-z]+$";

        if(shortUrl == null || shortUrl.isBlank()) {
            return false;
        }

        return shortUrl.length() == 7 && shortUrl.matches(BASE62_PATTERN_REGEX);
    }

}
