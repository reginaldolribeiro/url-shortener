package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.url.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlNullableException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlSanitizer;
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
                "http://",
                "http://  "
        })
        @DisplayName("Should throw InvalidUrlException for URL with missing or blank host")
        void shouldThrowExceptionForUrlWithMissingOrBlankHost(String url) {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(url));
        }

        @Test
        @DisplayName("Should throw InvalidUrlException for malformed domain")
        void shouldThrowInvalidUrlExceptionForMalformedDomain() {
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize("https://invalid_domain"));
        }

        @Test
        @DisplayName("Should throw InvalidUrlException for well-formed URL without valid host")
        void shouldThrowExceptionForMalformedHost() {
            String malformedUrl = "https://%20example.com";
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(malformedUrl));
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
                "https://example!@#.com/long-url",          // Multiple special characters in domain
                "https://example.com/invalid path",
                "https://example.com/invalid@path!",
                "https://example.com/invalid%ZZpath",
                "https://example.com/<script>alert()</script>"
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

    @Nested
    @DisplayName("isValidShortUrlCode Tests")
    class IsValidShortUrlCodeTests {

        @Test
        @DisplayName("Should return false for null input")
        void shouldReturnFalseForNullInput() {
            assertFalse(urlSanitizer.isValidShortUrlCode(null));
        }

        @Test
        @DisplayName("Should return false for blank input")
        void shouldReturnFalseForBlankInput() {
            assertFalse(urlSanitizer.isValidShortUrlCode(" "));
        }

        @Test
        @DisplayName("Should return false for input shorter than 7 characters")
        void shouldReturnFalseForShortInput() {
            assertFalse(urlSanitizer.isValidShortUrlCode("abc123"));
        }

        @Test
        @DisplayName("Should return false for input longer than 7 characters")
        void shouldReturnFalseForLongInput() {
            assertFalse(urlSanitizer.isValidShortUrlCode("abc12345"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc123!", "123@456", "ABcd*123"})
        @DisplayName("Should return false for input with invalid characters")
        void shouldReturnFalseForInputWithInvalidCharacters(String invalidCode) {
            assertFalse(urlSanitizer.isValidShortUrlCode(invalidCode));
        }

        @ParameterizedTest
        @ValueSource(strings = {"abc1234", "ABCDEFG", "1234567", "abcDEFG"})
        @DisplayName("Should return true for valid Base62 7-character input")
        void shouldReturnTrueForValidBase62Input(String validCode) {
            assertTrue(urlSanitizer.isValidShortUrlCode(validCode));
        }

        @Test
        @DisplayName("Should throw InvalidUrlException for malformed URL in sanitize")
        void shouldThrowExceptionForMalformedUrlInSanitize() {
            String malformedUrl = "https://example%.com";
            assertThrows(InvalidUrlException.class, () -> urlSanitizer.sanitize(malformedUrl));
        }
    }

}