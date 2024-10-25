package com.reginaldolribeiro.url_shortener.adapter.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static String toString(LocalDateTime localDateTime){
        return localDateTime.format(ISO_FORMATTER);
    }

    public static LocalDateTime parse(String dateTimeString){
        return LocalDateTime.parse(dateTimeString, ISO_FORMATTER);
    }

}
