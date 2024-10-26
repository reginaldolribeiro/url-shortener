package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNullableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("URL Sanitizer Tests")
class UrlSanitizerTest {

    @InjectMocks
    private UrlSanitizer urlSanitizer;

    @Nested
    @DisplayName("Valid URL Tests")
    class ValidUrlTests{

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/long-url",
                "https://example.com",
                "http://example.com"
        })
        @DisplayName("Should sanitize and accept valid URLs")
        public void shouldAcceptValidUrls(String validUrl) {
            var sanitizedUrl = urlSanitizer.sanitize(validUrl);
            assertNotNull(sanitizedUrl);
            assertEquals(validUrl, sanitizedUrl);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com:8080/resource",
                "https://example.com:443",
                "https://example.com"
        })
        @DisplayName("Should accept URLs with valid ports")
        public void shouldAcceptUrlsWithPorts(String urlWithPort) {
            assertEquals(urlWithPort, urlSanitizer.sanitize(urlWithPort));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/search?q=test",
                "https://example.com/page#section"
        })
        @DisplayName("Should accept URLs with query and fragment")
        public void shouldAcceptUrlsWithQueryAndFragment(String url) {
            assertEquals(url, urlSanitizer.sanitize(url));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/path%20with%20space",       // Encoded space
                "https://example.com/%E2%9C%93",                 // URL with checkmark (✓) encoded
                "https://example.com/file%2Ehtml",               // Encoded period
                "https://example.com/%23section",                // Encoded hash (#)
                "https://example.com/query?name=John%20Doe",     // Encoded space in query parameter
                "https://example.com/search?q=%25percent",       // Encoded percent sign (%)
                "https://example.com/page%20with%20spaces"       // Path with multiple encoded spaces
        })
        @DisplayName("Should accept URLs with encoded characters")
        void shouldAcceptUrlsWithEncodedCharacters(String validUrl) {
            assertEquals(validUrl, urlSanitizer.sanitize(validUrl));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://192.168.1.1",                // IPv4 address
                "https://[2001:db8::1]"               // IPv6 address
        })
        @DisplayName("Should accept URLs with IP addresses")
        void shouldAcceptUrlsWithIpAddresses(String ipUrl) {
            assertEquals(ipUrl, urlSanitizer.sanitize(ipUrl));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com:65535",          // Unusual but valid port
                "https://example.com:12345"           // Another uncommon port
        })
        @DisplayName("Should accept URLs with unusual valid ports")
        void shouldAcceptUrlsWithUnusualValidPorts(String urlWithPort) {
            assertEquals(urlWithPort, urlSanitizer.sanitize(urlWithPort));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://a.b.c.d.e.f.example.com"     // Nested subdomains
        })
        @DisplayName("Should accept URLs with nested subdomains")
        void shouldAcceptUrlsWithNestedSubdomains(String nestedSubdomainUrl) {
            assertEquals(nestedSubdomainUrl, urlSanitizer.sanitize(nestedSubdomainUrl));
        }

    }


    @Nested
    @DisplayName("Invalid URL Tests")
    class InvalidUrlTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "}) // Test with white spaces and missing scheme
        @DisplayName("Should reject null, empty, blank, or malformed URLs")
        void shouldRejectNullEmptyBlankOrMalformedUrls(String url) {
            assertThrows(UrlNullableException.class, () -> urlSanitizer.sanitize(url));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "example.com/long-url", // Test with white spaces and missing scheme
                "https://example.com/long url"
        })
        @DisplayName("Should reject missing schema URLs")
        void shouldRejectMissingSchemaUrls(String url) {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(url));
        }

        @Test
        @DisplayName("Should reject missing schema URLs")
        void shouldRejectMissingSchemaUrls() {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize("example.com/long-url"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/long-url%",
                "https://example.com/invalid%url",
                "https://example.com/long url",
                "https://example.'com/long-url",           // Single quote in domain
                "https://exa mple.com/long-url",           // Space in domain
                "https://example.com/long-url@",           // Special character at the end
                "https://example.com/long-<script>-url",   // HTML tags in path
                "https://example..com/long-url",           // Double dot in domain
                "https://.example.com/long-url",           // Leading dot in domain
                "https://example.com:abc/long-url",        // Invalid port format
                "https://-example.com/long-url",           // Hyphen at the beginning of domain
                "https://example.com/long-url#frag#ment",  // Multiple fragments
                "https://example!@#.com/long-url"          // Multiple special characters in domain
        })
        @DisplayName("Should reject URLs with invalid characters or malformed structures")
        void shouldRejectUrlsWithInvalidCharacters(String invalidLongUrl) {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(invalidLongUrl));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "ftp://example.com/resource",
                "mailto:user@example.com",
                "file:///C:/path/to/file",
                "data:text/plain;base64,SGVsbG8sIFdvcmxkIQ%3D%3D",
                "gopher://example.com/1",
                "ws://example.com/socket"
        })
        @DisplayName("Should reject URLs with unsupported schemes")
        void shouldRejectUnsupportedSchemes(String unsupportedUrl) {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(unsupportedUrl));
        }

    }

}