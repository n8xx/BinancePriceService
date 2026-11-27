package com.senla.binancepriceservice.dto;

import java.math.BigDecimal;

public record BinanceAvgPriceResponse(int mins, BigDecimal price, long closeTime) {
}
