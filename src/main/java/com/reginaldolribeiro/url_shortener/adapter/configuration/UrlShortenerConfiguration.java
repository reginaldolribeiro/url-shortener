package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.reginaldolribeiro.url_shortener.app.port.*;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlShortenerConfiguration {

    private final UserRepositoryPort userRepositoryPort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;
    private final ConfigurationService configurationService;

    public UrlShortenerConfiguration(UserRepositoryPort userRepositoryPort,
                                     UrlRepositoryPort urlRepositoryPort,
                                     UrlCacheRepositoryPort urlCacheRepositoryPort,
                                     IdGeneratorPort idGeneratorPort,
                                     ConfigurationService configurationService) {
        this.userRepositoryPort = userRepositoryPort;
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
        this.idGeneratorPort = idGeneratorPort;
        this.configurationService = configurationService;
    }


    @Bean
    public CreateShortUrlPort createShortUrlPort() {
        return new CreateShortUrlUseCase(userRepositoryPort,
                urlRepositoryPort,
                urlCacheRepositoryPort,
                idGeneratorPort,
                configurationService);
    }

}
