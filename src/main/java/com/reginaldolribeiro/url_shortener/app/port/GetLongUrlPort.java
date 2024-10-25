package com.reginaldolribeiro.url_shortener.app.port;

public interface GetLongUrlPort {
    String execute(String shortenedUrl);
}
