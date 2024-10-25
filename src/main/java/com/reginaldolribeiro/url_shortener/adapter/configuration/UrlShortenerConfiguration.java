package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.reginaldolribeiro.url_shortener.app.port.*;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlUseCase;
import com.reginaldolribeiro.url_shortener.app.usecase.GetLongUrlUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlShortenerConfiguration {

    private final UserRepositoryPort userRepositoryPort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;

    public UrlShortenerConfiguration(UserRepositoryPort userRepositoryPort,
                                     UrlRepositoryPort urlRepositoryPort,
                                     UrlCacheRepositoryPort urlCacheRepositoryPort,
                                     IdGeneratorPort idGeneratorPort,
                                     ConfigurationService configurationService) {
        this.userRepositoryPort = userRepositoryPort;
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
        this.idGeneratorPort = idGeneratorPort;
    }


    @Bean
    public CreateShortUrlPort createShortUrlPort() {
        return new CreateShortUrlUseCase(userRepositoryPort,
                urlRepositoryPort,
                urlCacheRepositoryPort,
                idGeneratorPort);
    }

    @Bean
    public GetLongUrlPort getLongUrlPort(){
        return new GetLongUrlUseCase(urlRepositoryPort, urlCacheRepositoryPort);
    }

}
