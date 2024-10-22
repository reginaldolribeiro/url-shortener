package com.reginaldolribeiro.url_shortener.adapter.configuration;

import com.reginaldolribeiro.url_shortener.adapter.IdGenerator;
import com.reginaldolribeiro.url_shortener.adapter.UrlCacheRepository;
import com.reginaldolribeiro.url_shortener.adapter.UrlRepositoryDatabase;
import com.reginaldolribeiro.url_shortener.adapter.UserRepositoryDatabase;
import com.reginaldolribeiro.url_shortener.app.port.*;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlShortenerConfiguration {

    /*@Bean
    public UserRepositoryPort userRepositoryPort() {
        return new UserRepositoryDatabase();
    }

    @Bean
    public UrlRepositoryPort urlRepositoryPort() {
        return new UrlRepositoryDatabase();
    }

    @Bean
    public UrlCacheRepositoryPort urlCacheRepositoryPort() {
        return new UrlCacheRepository();
    }

    @Bean
    public IdGeneratorPort idGeneratorPort() {
        return new IdGenerator();
    }

    @Bean
    public CreateShortUrlPort createShortUrlPort(UserRepositoryPort userRepositoryPort,
                                                 UrlRepositoryPort urlRepositoryPort,
                                                 UrlCacheRepositoryPort urlCacheRepositoryPort,
                                                 IdGeneratorPort idGeneratorPort) {
        return new CreateShortUrlUseCase(userRepositoryPort, urlRepositoryPort, urlCacheRepositoryPort, idGeneratorPort);
    }*/

    private final UserRepositoryPort userRepositoryPort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;

    public UrlShortenerConfiguration(UserRepositoryPort userRepositoryPort, UrlRepositoryPort urlRepositoryPort, UrlCacheRepositoryPort urlCacheRepositoryPort, IdGeneratorPort idGeneratorPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
        this.idGeneratorPort = idGeneratorPort;
    }


    @Bean
    public CreateShortUrlPort createShortUrlPort() {
        return new CreateShortUrlUseCase(userRepositoryPort, urlRepositoryPort, urlCacheRepositoryPort, idGeneratorPort);
    }

}
