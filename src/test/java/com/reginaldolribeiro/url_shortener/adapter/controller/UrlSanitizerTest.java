package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNullableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlSanitizerTest {

    @InjectMocks
    private UrlSanitizer urlSanitizer;

    @Test()
    public void testShouldReturnAnUrlWhenIsValid() {
        var validUrl = "https://example.com/long-url";
        var sanitizedUrl = urlSanitizer.sanitize(validUrl);
        assertNotNull(sanitizedUrl);
        assertEquals(sanitizedUrl, validUrl);
    }

    @Test
    public void testShouldAcceptHttpAndHttpsSchemes() {
        String httpUrl = "http://example.com";
        String httpsUrl = "https://example.com";
        assertEquals(httpUrl, urlSanitizer.sanitize(httpUrl));
        assertEquals(httpsUrl, urlSanitizer.sanitize(httpsUrl));
    }

    @Test
    public void testShouldAcceptUrlsWithPorts() {
        var urlWithPort = "https://example.com:8080/resource";
        assertEquals(urlWithPort, urlSanitizer.sanitize(urlWithPort));
    }

    @Test
    public void testShouldAcceptUrlsWithQueryAndFragment() {
        String urlWithQuery = "https://example.com/search?q=test";
        String urlWithFragment = "https://example.com/page#section";
        assertEquals(urlWithQuery, urlSanitizer.sanitize(urlWithQuery));
        assertEquals(urlWithFragment, urlSanitizer.sanitize(urlWithFragment));
    }

    @Test()
    public void testShouldRejectWhenUrlIsNull() {
        assertThrows(UrlNullableException.class, () -> urlSanitizer.sanitize(null));
    }

    @Test()
    public void testShouldRejectWhenUrlIsEmpty() {
        assertThrows(UrlNullableException.class, () -> urlSanitizer.sanitize(""));
    }

    @Test
    public void testShouldRejectWhenUrlIsInvalid() {
        String invalidLongUrl = "example.com/long-url";
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(invalidLongUrl));
    }

    @Test
    public void testShouldRejectUrlsWithInvalidCharacters() {
        var invalidLongUrl = "https://example.com/long-url%";
        var urlWithInvalidChar = "https://example.com/invalid%url";
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(invalidLongUrl));
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(urlWithInvalidChar));
    }

    @Test
    public void testShouldRejectUrlsWithSpaces() {
        var urlWithSpaces = "https://example.com/long url";
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(urlWithSpaces));
    }

    @Test
    public void testShouldRejectUnsupportedSchemes() {
        String ftpUrl = "ftp://example.com/resource";
        String mailtoUrl = "mailto:user@example.com";
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(ftpUrl));
        assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(mailtoUrl));
    }

}