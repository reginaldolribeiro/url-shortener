package com.reginaldolribeiro.url_shortener.app.usecase.url;

import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlDisabledException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.UrlNotFoundException;
import com.reginaldolribeiro.url_shortener.app.exception.ShortUrlMalformedException;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;

public class GetLongUrlUseCase implements GetLongUrlPort {

    private final UrlRepositoryPort urlRepositoryPort;

    public GetLongUrlUseCase(UrlRepositoryPort urlRepositoryPort) {
        this.urlRepositoryPort = urlRepositoryPort;
    }
//    private final UrlCacheRepositoryPort urlCacheRepositoryPort;

//    public GetLongUrlUseCase(UrlRepositoryPort urlRepositoryPort, UrlCacheRepositoryPort urlCacheRepositoryPort) {
//        this.urlRepositoryPort = urlRepositoryPort;
//        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
//    }

    @Override
    public String execute(String shortenedUrl) {
        if(!isValidShortUrlCode(shortenedUrl))
            throw new ShortUrlMalformedException("Short URL code must be in a valid format.");

        return urlRepositoryPort
                .findByShortenedUrl(shortenedUrl)
                .map(url -> {
                    if (!url.isActive()) {
                        throw new UrlDisabledException("URL is disabled.");
                    }
                    return url.getLongUrl();
                })
                .orElseThrow(() -> new UrlNotFoundException("URL " + shortenedUrl + " not found."));
    }


    public boolean isValidShortUrlCode(String shortUrl) {
        if(shortUrl == null || shortUrl.isBlank()) {
            return false;
        }
        return shortUrl.length() == 7 && shortUrl.matches("^[0-9A-Za-z]+$");
    }

//    @Override
//    public String execute(String shortenedUrl) {
//        var cachedUrl = urlCacheRepositoryPort
//                .findByUrlId(shortenedUrl)
//                .filter(Url::isActive).stream().findFirst();
//
//        if (cachedUrl.isPresent()) {
//            return cachedUrl.get().getLongUrl();
//        } else {
//            var url = urlRepositoryPort
//                    .findByShortenedUrl(shortenedUrl)
//                    .orElseThrow(() -> new UrlNotFoundException("URL " + shortenedUrl + " not found."));
//
//            if (url.isActive()) {
//                return url.getLongUrl();
//            } else {
//                throw new UrlDisabledException("URL is disabled.");
//            }
//        }
//    }

}
