package com.reginaldolribeiro.url_shortener.adapter.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateShortUrlRequest(
        @NotNull @NotBlank String userId,
        @NotNull @NotBlank String longUrl
//        @NotNull @NotBlank @Pattern(regexp = "^(http|https)://.*$") String longUrl
) {
}
