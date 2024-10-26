package com.reginaldolribeiro.url_shortener.adapter.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DateTimeHelper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String toString(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(date -> date.format(ISO_FORMATTER))
                .orElseThrow(() -> new IllegalArgumentException("LocalDateTime cannot be null"));
    }

    public static LocalDateTime parse(String dateTimeString) {
        return Optional.ofNullable(dateTimeString)
                .filter(dateStr -> !dateStr.isBlank())
                .map(dateStr -> {
                    try {
                        return LocalDateTime.parse(dateStr, ISO_FORMATTER);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid date time format. Expected ISO format.", e);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Date time string cannot be null or blank"));
    }

}
