package com.senla.binancepriceservice.client;

import com.senla.binancepriceservice.config.BinanceProperties;
import com.senla.binancepriceservice.dto.BinanceAvgPriceResponse;
import com.senla.binancepriceservice.exception.BinanceClientException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class BinanceClient {

    private final BinanceProperties properties;
    private final RestClient restClient;

    public BinanceClient(BinanceProperties properties, RestClient binanceRestClient) {
        this.properties = properties;
        this.restClient = binanceRestClient;
    }

    public BinanceAvgPriceResponse getAveragePrice(String symbol) {
        BinanceAvgPriceResponse response;
        try {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(properties.avgPricePath())
                            .queryParam("symbol", symbol)
                            .build())
                    .retrieve()
                    .body(BinanceAvgPriceResponse.class);
        } catch (RestClientResponseException e) {
            throw new BinanceClientException("Binance returned HTTP %d for symbol %s: %s"
                    .formatted(e.getStatusCode().value(), symbol, e.getResponseBodyAsString()), e);
        } catch (RestClientException e) {
            throw new BinanceClientException("Failed to call Binance for symbol " + symbol, e);
        }

        if (response == null || response.price() == null || response.closeTime() <= 0) {
            throw new BinanceClientException("Binance returned an invalid response for symbol " + symbol);
        }
        return response;
    }
}
