package com.reginaldolribeiro.url_shortener.adapter.controller.url;

import com.reginaldolribeiro.url_shortener.FixtureTests;
import com.reginaldolribeiro.url_shortener.adapter.configuration.JacksonConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JacksonConfig.class)
class CreateShortUrlResponseTest {

    @Autowired
    private JacksonTester<CreateShortUrlResponse> json;

    @Nested
    @DisplayName("CreateShortUrlResponse should Serialize to JSON in snake_case")
    class ShouldSerialize {

        static final CreateShortUrlResponse CREATE_SHORT_URL_RESPONSE = FixtureTests.sampleCreateShortUrlResponse();

        @Test
        @DisplayName("CreateShortUrlResponse should serialize to JSON in snake_case")
        void shouldSerializeToSnakeCase() throws Exception {
            var result = json.write(CREATE_SHORT_URL_RESPONSE);

            assertThat(result).hasJsonPathStringValue("$.long_url", CREATE_SHORT_URL_RESPONSE.longUrl());
            assertThat(result).hasJsonPathStringValue("$.shortened_url", CREATE_SHORT_URL_RESPONSE.shortenedUrl());
        }

    }

    @Nested
    @DisplayName("CreateShortUrlResponse should Deserialize from JSON in snake_case")
    class ShouldDeserialize {

        @Test
        @DisplayName("CreateShortUrlResponse should deserialize from JSON in snake_case")
        void shouldDeserializeFromSnakeCase() throws Exception {
            String jsonContent = """
            {
                "long_url": "https://example.com/long-url",
                "shortened_url": "https://short.url/abc123"
            }
            """;

            var createShortUrlResponse = json.parseObject(jsonContent);

            assertThat(createShortUrlResponse.longUrl()).isEqualTo("https://example.com/long-url");
            assertThat(createShortUrlResponse.shortenedUrl()).isEqualTo("https://short.url/abc123");
        }

        @Test
        @DisplayName("CreateShortUrlResponse should ignore extra fields in JSON")
        void shouldIgnoreExtraFields() throws Exception {
            String jsonContent = """
            {
                "long_url": "https://example.com/long-url",
                "shortened_url": "https://short.url/abc123",
                "extra_field": "extra_value"
            }
            """;

            var createShortUrlResponse = json.parseObject(jsonContent);

            assertThat(createShortUrlResponse.longUrl()).isEqualTo("https://example.com/long-url");
            assertThat(createShortUrlResponse.shortenedUrl()).isEqualTo("https://short.url/abc123");
        }
    }

}