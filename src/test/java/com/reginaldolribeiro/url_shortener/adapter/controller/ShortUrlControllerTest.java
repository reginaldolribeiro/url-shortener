package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlNotFoundException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.CreateShortUrlRequest;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.ShortUrlController;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlSanitizer;
import com.reginaldolribeiro.url_shortener.app.domain.User;
import com.reginaldolribeiro.url_shortener.app.port.CreateShortUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlInput;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShortUrlController.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlSanitizer urlSanitizer;
    @MockBean
    private GetLongUrlPort getLongUrlPort;
    @MockBean
    private CreateShortUrlPort createShortUrlPort;

    private static final String ORIGINAL_URL = FixtureTests.DEFAULT_LONG_URL;
    private static final User USER = FixtureTests.createSampleUser();
    private final String USER_ID = USER.getId().toString();

    @Nested
    @DisplayName("Valid createShortUrl Requests")
    class ValidCreateShortUrlRequests{

        @ParameterizedTest
        @CsvSource({
                "'5vsRJSy'",
                "'Ab12Cd3'",
                "'XyZ987a'"
        })
        @DisplayName("Should return Shortened URL for valid Long URLs")
        void shouldReturnAShortenedUrlWhenHasValidInput(String shortCode) throws Exception {
            var input = new CreateShortUrlInput(USER_ID, ORIGINAL_URL);
            var output = new CreateShortUrlOutput(USER_ID, shortCode, ORIGINAL_URL);
            var createShortUrlRequest = new CreateShortUrlRequest(USER_ID, ORIGINAL_URL);
            var request = objectMapper.writer().writeValueAsString(createShortUrlRequest);

            when(urlSanitizer.sanitize(ORIGINAL_URL)).thenReturn(ORIGINAL_URL);
            when(createShortUrlPort.execute(input)).thenReturn(output);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/short-url")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(request)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.long_url").value(ORIGINAL_URL))
                    .andExpect(jsonPath("$.shortened_url").value(startsWith(FixtureTests.DEFAULT_DOMAIN)))
                    .andExpect(jsonPath("$.shortened_url").value(endsWith(shortCode)))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.long_url").isString())
                    .andExpect(jsonPath("$.shortened_url").isString())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(urlSanitizer, times(1)).sanitize(ORIGINAL_URL);
            verify(createShortUrlPort, times(1)).execute(input);
        }

    }

    @Nested
    @DisplayName("Invalid createShortUrl Requests")
    class InvalidCreateShortUrlRequests{

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"    "}) // Test with white spaces
        @DisplayName("Should return Bad Request Error for null, empty and blank Long Urls")
        void shouldReturnBadRequestErrorForANullLongUrl(String invalidLongUrl) throws Exception {
            var createShortUrlRequest = new CreateShortUrlRequest(USER_ID, invalidLongUrl);
            var request = objectMapper.writer().writeValueAsString(createShortUrlRequest);

            mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/short-url")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(request)
                    )
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.longUrl").value("must not be blank"))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(urlSanitizer, times(0)).sanitize(ORIGINAL_URL);
            verify(createShortUrlPort, times(0)).execute(any(CreateShortUrlInput.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "https://example.com/very-'long-url",  // Special character '
                "ftp://example.com/resource",          // Unsupported scheme (FTP)
                "http://",                             // Incomplete URL
                "http:/example.com",                   // Missing slash
                "://example.com",                      // Missing scheme
                "http://exa mple.com",                 // Space in URL
                "example.com",                         // Missing scheme (no "http://")
                "https://",                            // Scheme with no domain
                "http://.com",                         // Invalid domain format
                "https://example..com"                 // Double dots in domain
        })
        @DisplayName("Should return a Bad Request Error for an invalid Long Url")
        void shouldReturnBadRequestErrorForAnInvalidInput(String invalidLongUrl) throws Exception {
            var createShortUrlRequest = new CreateShortUrlRequest(USER_ID, invalidLongUrl);
            var request = objectMapper.writeValueAsString(createShortUrlRequest);

            when(urlSanitizer.sanitize(invalidLongUrl)).thenThrow(new InvalidUrlException(anyString()));

            mockMvc.perform(
                    MockMvcRequestBuilders
                            .post("/short-url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(request)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400));

            verify(urlSanitizer, times(1)).sanitize(invalidLongUrl);
            verify(createShortUrlPort, times(0)).execute(any(CreateShortUrlInput.class));
        }


        @Test
        @DisplayName("Should return the same Shortened URL for duplicate Long Urls")
        void shouldReturnSameShortenedUrlForDuplicateLongUrls() {
            // Test implementation
        }

        @Test
        @DisplayName("Should return Bad Request Error for excessively long Long Urls")
        void shouldReturnBadRequestErrorForExcessivelyLongLongUrls() {
            // Test implementation
        }

        @Test
        @DisplayName("Should return Unauthorized Error when user is not authenticated")
        void shouldReturnUnauthorizedErrorWhenUserIsNotAuthenticated() {
            // Test implementation
        }

    }

    @Nested
    @DisplayName("Valid getOriginalUrl Requests")
    class ValidGetOriginalUrlRequests {

        @ParameterizedTest
        @CsvSource({
                "'5vsRJSy'",
                "'Ab12Cd3'",
                "'XyZ987a'"
        })
        @DisplayName("Should return Location header for valid short URL")
        void shouldReturnLocationHeader(String shortUrlCode) throws Exception {

            when(getLongUrlPort.execute(shortUrlCode)).thenReturn(ORIGINAL_URL);

            mockMvc.perform(get("/short-url/{id}", shortUrlCode))
                    .andDo(print())
                    .andExpect(status().isMovedPermanently())
                    .andExpect(header().string("Location", ORIGINAL_URL))
                    .andExpect(content().string(""))
                    .andExpect(header().doesNotExist("Content-Type"));

            verify(getLongUrlPort, times(1)).execute(shortUrlCode);
        }

    }

    @Nested
    @DisplayName("Invalid getOriginalUrl Requests")
    class InvalidGetOriginalUrlRequests {

        @Test
        @DisplayName("Should return Bad Request error for blank short URL")
        void shouldReturnBadRequestErrorForBlankShortUrl() throws Exception {
            var blankShortUrlCode = " ";
            mockMvc.perform(get("/short-url/{id}", blankShortUrlCode))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Constraint violations"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors['getOriginalUrl.shortUrl']").value(anyOf(
                            is("Short URL cannot be blank"),
                            is("Short URL must be exactly 7 characters long"),
                            is("Short URL must be in Base62 format")
                    ))) // We use anyOf because the validation order is not guaranteed, so either message may appear first.
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(urlSanitizer, times(0)).sanitize(ORIGINAL_URL);
            verify(createShortUrlPort, times(0)).execute(any(CreateShortUrlInput.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "a", // Too short
                "2cnb", // Too short
                "ABCDEFGH",// Too long
        })
        @DisplayName("Should return Bad Request error for short URL with invalid length")
        void shouldReturnBadRequestErrorForShortUrlWithInvalidLength(String invalidShortUrl) throws Exception {
            mockMvc.perform(get("/short-url/{id}", invalidShortUrl))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Constraint violations"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors['getOriginalUrl.shortUrl']").value("Short URL must be exactly 7 characters long"))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(urlSanitizer, times(0)).sanitize(ORIGINAL_URL);
            verify(createShortUrlPort, times(0)).execute(any(CreateShortUrlInput.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "5vsRJS+", // Invalid character '+'
                "5vsR JS", // Space character
                "5vsR@JS"  // Special character '@'
        })
        @DisplayName("Should return Bad Request error for short URLs with invalid formats")
        void shouldReturnBadRequestErrorForInvalidShortUrls(String invalidShortUrl) throws Exception {
            mockMvc.perform(get("/short-url/{id}", invalidShortUrl))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Constraint violations"))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors['getOriginalUrl.shortUrl']").value("Short URL must be in Base62 format"))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            // Verify no interactions since input is invalid
            verify(getLongUrlPort, times(0)).execute(any(String.class));
            verify(urlSanitizer, times(0)).isValidShortUrlCode(any(String.class));
        }

        @Test
        @DisplayName("Should return Not Found error when short URL does not exist")
        void shouldReturnNotFoundErrorWhenShortUrlDoesNotExist() throws Exception {
            when(urlSanitizer.sanitize(ORIGINAL_URL)).thenReturn(ORIGINAL_URL);
            when(getLongUrlPort.execute(FixtureTests.SHORT_URL_CODE)).thenThrow(new UrlNotFoundException("Short URL not found."));

            mockMvc.perform(get("/short-url/{id}", FixtureTests.SHORT_URL_CODE))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Short URL not found."))
                    .andExpect(jsonPath("$.errors").isNotEmpty())
                    .andExpect(jsonPath("$.errors.message").value("Short URL not found."))
                    .andExpect(jsonPath("$").isMap())
                    .andExpect(jsonPath("$.status").isNumber())
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.timestamp").isString())
                    .andExpect(jsonPath("$.length()").value(4));

            verify(getLongUrlPort, times(1)).execute(anyString());
        }

    }

}