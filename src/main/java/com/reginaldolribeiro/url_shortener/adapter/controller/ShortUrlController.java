package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.app.port.CreateShortUrlPort;
import com.reginaldolribeiro.url_shortener.app.usecase.CreateShortUrlInput;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/short-url")
public class ShortUrlController {

    private final CreateShortUrlPort createShortUrlPort;

    public ShortUrlController(CreateShortUrlPort createShortUrlPort) {
        this.createShortUrlPort = createShortUrlPort;
    }

    @PostMapping
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request){
        var output = createShortUrlPort.execute(
                new CreateShortUrlInput(
                        request.userId(),
                        request.longUrl()
                )
        );
        var response = new CreateShortUrlResponse(output.longUrl(), output.shortUrl());
        return new ResponseEntity<CreateShortUrlResponse>(response, HttpStatus.CREATED);
    }

}
