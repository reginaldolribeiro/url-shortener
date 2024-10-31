package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.domain.User;

public class UrlMapper {

    public static UrlEntity toEntity(Url url) {
        if (url == null)
            return null;

        return new UrlEntity.UrlEntityBuilder()
                .shortUrlId(url.getId())
                .longUrl(url.getLongUrl())
                .createdAt(url.getCreatedAt())
                .updatedAt(url.getUpdatedAt())
                .userId(getUserIdAsString(url.getUser()))
                .clicks(url.getClicks())
                .active(url.isActive())
                .build();
    }

    public static Url toDomain(UrlEntity urlEntity, User user) {
        if (urlEntity == null)
            return null;

        return new Url.Builder()
                .id(urlEntity.getShortUrlId())
                .longUrl(urlEntity.getLongUrl())
                .createdAt(urlEntity.getCreatedAt())
                .updatedAt(urlEntity.getUpdatedAt())
                .user(user)
                .clicks(urlEntity.getClicks())
                .active(urlEntity.isActive())
                .build();
    }

    private static String getUserIdAsString(User user) {
        if(user != null && user.getId() != null){
            return user.getId().toString();
        }
        return null;
    }

}
