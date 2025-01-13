package com.reginaldolribeiro.url_shortener.adapter.controller.url;

import com.reginaldolribeiro.url_shortener.app.port.CreateShortUrlPort;
import com.reginaldolribeiro.url_shortener.app.port.GetLongUrlPort;
import com.reginaldolribeiro.url_shortener.app.usecase.url.CreateShortUrlInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RestController()
@RequestMapping("/short-url")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ShortUrlController implements ShortUrlApiDocs {

    private final UrlSanitizer urlSanitizer;
    private final CreateShortUrlPort createShortUrlPort;
    private final GetLongUrlPort getLongUrlPort;

    @Value("${app.domain-name-prefix}")
    private String baseUrl;

    public ShortUrlController(UrlSanitizer urlSanitizer,
                              CreateShortUrlPort createShortUrlPort,
                              GetLongUrlPort getLongUrlPort) {
        this.urlSanitizer = urlSanitizer;
        this.createShortUrlPort = createShortUrlPort;
        this.getLongUrlPort = getLongUrlPort;
    }

    @PostMapping
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request) {
        var sanitizedUrl = urlSanitizer.sanitize(request.longUrl());
        var output = createShortUrlPort.execute(
                new CreateShortUrlInput(
                        request.userId(),
                        sanitizedUrl
                )
        );
        var fullShortenedUrl = baseUrl + output.shortUrl();
        var response = new CreateShortUrlResponse(output.longUrl(), fullShortenedUrl);
        return new ResponseEntity<CreateShortUrlResponse>(response, HttpStatus.CREATED);
    }

    @GetMapping("{short_url}")
    public ResponseEntity<?> getOriginalUrl(
            @PathVariable("short_url")
            @NotBlank(message = "Short URL cannot be blank")
            @Size(min = 7, max = 7, message = "Short URL must be exactly 7 characters long")
            @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "Short URL must be in Base62 format")
            String shortUrl){

        var originalUrl = getLongUrlPort.execute(shortUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(originalUrl))
                .build();
    }

}
