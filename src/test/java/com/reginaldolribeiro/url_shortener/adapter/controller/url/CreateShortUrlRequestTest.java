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
@ContextConfiguration(classes = JacksonConfig.class) // Load custom ObjectMapper from JacksonConfig
class CreateShortUrlRequestTest {

    @Autowired
    private JacksonTester<CreateShortUrlRequest> json;

    @Nested
    @DisplayName("CreateShortUrlRequest should Serialize to JSON in snake_case")
    class ShouldSerialize {

        static final CreateShortUrlRequest CREATE_SHORT_URL_REQUEST = FixtureTests.sampleCreateShortUrlRequest();

        @Test
        @DisplayName("CreateShortUrlRequest should serialize to JSON in snake_case")
        void shouldSerializeToSnakeCase() throws Exception {
            var result = json.write(CREATE_SHORT_URL_REQUEST);

            assertThat(result).hasJsonPathStringValue("$.user_id", CREATE_SHORT_URL_REQUEST.userId());
            assertThat(result).hasJsonPathStringValue("$.long_url", CREATE_SHORT_URL_REQUEST.longUrl());
        }

        @Test
        @DisplayName("CreateShortUrlRequest should include required fields in JSON")
        void shouldIncludeRequiredFields() throws Exception {
            var result = json.write(CREATE_SHORT_URL_REQUEST);

            assertThat(result).hasJsonPathValue("$.user_id");
            assertThat(result).hasJsonPathValue("$.long_url");
        }
    }

    @Nested
    @DisplayName("CreateShortUrlRequest should Deserialize from JSON in snake_case")
    class ShouldDeserialize {

        @Test
        @DisplayName("CreateShortUrlRequest should deserialize from JSON in snake_case")
        void shouldDeserializeFromSnakeCase() throws Exception {
            String jsonContent = """
            {
                "user_id": "123e4567-e89b-12d3-a456-426614174000",
                "long_url": "https://example.com/long-url"
            }
            """;

            var createShortUrlRequest = json.parseObject(jsonContent);

            assertThat(createShortUrlRequest.userId()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
            assertThat(createShortUrlRequest.longUrl()).isEqualTo("https://example.com/long-url");
        }

        @Test
        @DisplayName("CreateShortUrlRequest should ignore extra fields in JSON")
        void shouldIgnoreExtraFields() throws Exception {
            String jsonContent = """
            {
                "user_id": "123e4567-e89b-12d3-a456-426614174000",
                "long_url": "https://example.com/long-url",
                "extra_field": "extra_value"
            }
            """;

            var createShortUrlRequest = json.parseObject(jsonContent);

            assertThat(createShortUrlRequest.userId()).isEqualTo("123e4567-e89b-12d3-a456-426614174000");
            assertThat(createShortUrlRequest.longUrl()).isEqualTo("https://example.com/long-url");
        }
    }

}