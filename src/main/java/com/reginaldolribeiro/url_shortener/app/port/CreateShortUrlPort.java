package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlInput;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlOutput;

public interface CreateShortUrlPort {
    CreateShortUrlOutput execute(CreateShortUrlInput input);
}
