package com.reginaldolribeiro.url_shortener.adapter.controller.url;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ShortUrlApiDocs {

    @Operation(summary = "Redirect to the original long URL",
            description = "Returns a 301 redirect to the original long URL.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "301",
                    description = "Redirect to the original long URL",
                    headers = @Header(
                            name = HttpHeaders.LOCATION,
                            description = "URL to redirect to",
                            schema = @Schema(type = "string", example = "https://example.com/very-long-url1")
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "410", description = "Short URL has been disabled"),
    })
    ResponseEntity<?> getOriginalUrl(
            @PathVariable("short_url")
            @NotBlank(message = "Short URL cannot be blank")
            @Size(min = 7, max = 7, message = "Short URL must be exactly 7 characters long")
            @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "Short URL must be in Base62 format")
            String shortUrl);

    @Operation(summary = "Create a shortened URL", description = "Creates a shortened URL for the given long URL and user ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(
                            schema = @Schema(implementation = CreateShortUrlResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad request - Validation error or invalid URL"),
            @ApiResponse(responseCode = "404",description = "User not found"),
            @ApiResponse(responseCode = "412",description = "ID generation error - Precondition failed"),
    })
    ResponseEntity<CreateShortUrlResponse> createShortUrl(@Valid @RequestBody CreateShortUrlRequest request);

}
