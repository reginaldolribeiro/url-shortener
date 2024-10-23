package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.reginaldolribeiro.url_shortener.app.port.ConfigurationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${app.domain-name-prefix}")
    private String baseUrl;

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
