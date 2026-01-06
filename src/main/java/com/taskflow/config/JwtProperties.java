package com.taskflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Configuration properties for JWT authentication.
 * Maps to 'jwt.*' properties in application.yml.
 */
@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtProperties(

    @NotBlank
    String secret,

    @Positive
    long expiration,

    @Positive
    long refreshExpiration
) {
}
