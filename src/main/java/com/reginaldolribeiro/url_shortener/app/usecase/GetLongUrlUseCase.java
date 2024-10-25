package com.reginaldolribeiro.url_shortener.app.usecase;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlDisabledException;
import com.reginaldolribeiro.url_shortener.adapter.controller.exception.UrlNotFoundException;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlCacheRepositoryPort;
import com.reginaldolribeiro.url_shortener.app.port.UrlRepositoryPort;

public class GetLongUrlUseCase implements GetLongUrlPort {

    private final UrlRepositoryPort urlRepositoryPort;
    private final UrlCacheRepositoryPort urlCacheRepositoryPort;

    public GetLongUrlUseCase(UrlRepositoryPort urlRepositoryPort, UrlCacheRepositoryPort urlCacheRepositoryPort) {
        this.urlRepositoryPort = urlRepositoryPort;
        this.urlCacheRepositoryPort = urlCacheRepositoryPort;
    }

    @Override
    public String execute(String shortenedUrl) {
        return urlCacheRepositoryPort
                .findByUrlId(shortenedUrl)
                .filter(Url::isActive)
                .map(Url::getLongUrl)
                .orElseGet(() -> findActiveUrlFromDatabase(shortenedUrl));
    }

    private String findActiveUrlFromDatabase(String shortenedUrl) {
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