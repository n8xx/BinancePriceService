package com.senla.binancepriceservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "binance.api")
public record BinanceProperties(
        @NotBlank String baseUrl,
        @NotBlank String avgPricePath,
        @NotBlank String symbol,
        @NotNull Duration connectTimeout,
        @NotNull Duration readTimeout,
        @NotNull Duration pollInterval) {
}
