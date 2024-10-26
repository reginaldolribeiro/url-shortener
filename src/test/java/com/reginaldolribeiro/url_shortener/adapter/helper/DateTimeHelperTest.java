package com.reginaldolribeiro.url_shortener.adapter.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DateTimeHelper Tests")
class DateTimeHelperTest {

    @Nested
    @DisplayName("toString Method Tests")
    class ToStringMethodTests {

        @Test
        @DisplayName("Should format LocalDateTime to ISO String")
        void shouldFormatLocalDateTimeToIsoString() {
            LocalDateTime dateTime = LocalDateTime.of(2023, 10, 25, 14, 30, 15);
            String formattedDate = DateTimeHelper.toString(dateTime);
            assertEquals("2023-10-25T14:30:15", formattedDate);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Should throw IllegalArgumentException for null LocalDateTime")
        void shouldThrowIllegalArgumentExceptionForNullLocalDateTime(LocalDateTime nullDateTime) {
            assertThrows(IllegalArgumentException.class,
                    () -> DateTimeHelper.toString(nullDateTime));
        }

    }

    @Nested
    @DisplayName("parse Method Tests")
    class ParseMethodTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "2023-10-25T14:30:15",
                "2022-01-01T00:00:00",
                "2030-12-31T23:59:59"
        })
        @DisplayName("Should parse valid ISO formatted string to LocalDateTime")
        void shouldParseValidIsoStringToLocalDateTime(String isoDateString) {
            LocalDateTime dateTime = DateTimeHelper.parse(isoDateString);
            assertEquals(isoDateString, DateTimeHelper.toString(dateTime),
                    "Expected parsed LocalDateTime to match original ISO string");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Should throw IllegalArgumentException for null, empty, or blank dateTimeString")
        void shouldThrowIllegalArgumentExceptionForInvalidDateTimeString(String dateTimeString) {
            assertThrows(IllegalArgumentException.class,
                    () -> DateTimeHelper.parse(dateTimeString));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "25-10-2023T14:30:15",    // Invalid date format
                "2023-10-25 14:30:15",    // Space instead of 'T'
                "2023-10-25T24:00:00"     // Invalid hour
        })
        @DisplayName("Should throw DateTimeParseException for invalid ISO strings")
        void shouldThrowExceptionForInvalidIsoStrings(String invalidIsoString) {
            assertThrows(IllegalArgumentException.class,
                    () -> DateTimeHelper.parse(invalidIsoString));
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should return the original LocalDateTime after toString and parse")
        void shouldReturnOriginalLocalDateTimeAfterToStringAndParse() {
            LocalDateTime originalDateTime = LocalDateTime.of(2023, 10, 25, 14, 30, 15);
            String formattedDate = DateTimeHelper.toString(originalDateTime);
            LocalDateTime parsedDateTime = DateTimeHelper.parse(formattedDate);
            assertEquals(originalDateTime, parsedDateTime);
        }
    }
}