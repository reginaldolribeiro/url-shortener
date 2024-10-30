package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


/**
 * With only an ObjectMapper bean in JacksonConfig, the following options will all only load JacksonConfig and the ObjectMapper:
 * 1. @SpringBootTest(classes = JacksonConfig.class)
 * 2. @JsonTest with @Import(JacksonConfig.class)
 * 3. @ContextConfiguration(classes = JacksonConfig.class)
 */

@SpringBootTest(classes = JacksonConfig.class)
//@ContextConfiguration(classes = JacksonConfig.class)
//@JsonTest
//@Import(JacksonConfig.class)
class JacksonConfigTest {

    public record SampleObject(
            String nullableField,
            String nonNullableField,
            Boolean active
    ) {}

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Naming Strategy Tests")
    class NamingStrategyTests {

        @Test
        @DisplayName("ObjectMapper should use snake_case naming strategy")
        void shouldUseSnakeCaseNamingStrategy() {
            assertEquals(PropertyNamingStrategies.SNAKE_CASE, objectMapper.getPropertyNamingStrategy());
        }

        @Test
        @DisplayName("ObjectMapper should apply snake_case to nested objects")
        void shouldSerializeNestedObjectWithSnakeCase() throws Exception {
            var sample = new SampleObject("first", "second", true);
            var result = objectMapper.writeValueAsString(sample);
            assertAll(
                    () -> assertTrue(result.contains("nullable_field")),
                    () -> assertTrue(result.contains("non_nullable_field")),
                    () -> assertTrue(result.contains("active"))
            );
        }

        @Test
        @DisplayName("ObjectMapper should serialize null and empty fields in SampleObject")
        void shouldSerializeNullAndEmptyFieldsInSampleObject() throws Exception {
            SampleObject sample = new SampleObject(null, "", true);

            String jsonResult = objectMapper.writeValueAsString(sample);

            assertThat(jsonResult).contains("\"nullable_field\":null");
            assertThat(jsonResult).contains("\"non_nullable_field\":\"\"");
            assertThat(jsonResult).contains("\"active\":true");
        }

    }


    @Nested
    @DisplayName("Date Serialization Tests")
    class DateSerializationTests {

        @Test
        @DisplayName("ObjectMapper should use ISO-8601 for LocalDate serialization")
        void shouldUseISO8601ForDateFormat() throws Exception {
            var dateJson = objectMapper.writeValueAsString(LocalDate.of(2023, 10, 31));
            assertEquals("\"2023-10-31\"", dateJson);
        }

        @Test
        @DisplayName("ObjectMapper should use ISO-8601 for LocalDateTime serialization")
        void shouldSerializeLocalDateTimeInISO8601Format() throws Exception {
            var dateTimeJson = objectMapper.writeValueAsString(LocalDateTime.of(2023, 10, 31, 15, 45));
            assertEquals("\"2023-10-31T15:45:00\"", dateTimeJson);
        }

    }


    @Nested
    @DisplayName("Optional Handling Tests")
    class OptionalHandlingTests {

        @Test
        @DisplayName("ObjectMapper should handle Optional serialization correctly")
        void shouldHandleOptionalProperly() throws Exception {
            var result = objectMapper.writeValueAsString(Optional.empty());
            assertEquals("null", result);

            var presentOptional = objectMapper.writeValueAsString(Optional.of("example"));
            assertEquals("\"example\"", presentOptional);
        }

    }

}


