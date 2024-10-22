package com.reginaldolribeiro.url_shortener.app.port;

import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlInput;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlOutput;

public interface CreateShortUrlPort {
    public CreateShortUrlOutput execute(CreateShortUrlInput input);
}
