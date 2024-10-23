package com.reginaldolribeiro.url_shortener.adapter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

//@Configuration
public class TimeZoneConfig {

//    @Bean
    public void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}