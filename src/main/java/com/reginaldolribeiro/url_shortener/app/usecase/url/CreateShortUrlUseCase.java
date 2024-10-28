package com.reginaldolribeiro.url_shortener.app.usecase.url;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.*;

public class CreateShortUrlUseCase implements CreateShortUrlPort {

    private final UserRepositoryPort userRepositoryPort;
    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;
    private final IdGeneratorPort idGeneratorPort;

    public CreateShortUrlUseCase(UserRepositoryPort userRepositoryPort,
                                 UrlRepositoryPort urlRepositoryPort,
                                 UrlCacheRepositoryPort urlCacheRepositoryPort,
                                 IdGeneratorPort idGeneratorPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
        this.idGeneratorPort = idGeneratorPort;
    }

    @Override
    public CreateShortUrlOutput execute(CreateShortUrlInput input) {
        var user = userRepositoryPort.findById(input.userId())
                .orElseThrow(() -> new UserNotFoundException("User " + input.userId() + " not found."));

        var shortenedUrlId = idGeneratorPort.generate();
        var url = Url.create(shortenedUrlId, input.longUrl(), user);

        urlRepositoryPort.save(url);
        urlCacheRepositoryPort.save(url);

        return new CreateShortUrlOutput(
                url.getUser().getId().toString(),
                shortenedUrlId,
                input.longUrl()
        );
    }

}
