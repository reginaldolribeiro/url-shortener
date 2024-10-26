package com.reginaldolribeiro.url_shortener.adapter.controller;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlRequest(
        @NotBlank String userId,
        @NotBlank String longUrl
//        @NotBlank @Pattern(regexp = "^(http|https)://.*$") String longUrl
) {
}
