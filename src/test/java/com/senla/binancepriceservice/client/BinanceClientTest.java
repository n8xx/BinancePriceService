package com.senla.binancepriceservice.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.senla.binancepriceservice.config.BinanceClientConfig;
import com.senla.binancepriceservice.config.BinanceProperties;
import com.senla.binancepriceservice.dto.BinanceAvgPriceResponse;
import com.senla.binancepriceservice.exception.BinanceClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest
class BinanceClientTest {

    private static final String AVG_PRICE_PATH = "/api/v3/avgPrice";
    private static final String SYMBOL = "BNBUSDT";

    private BinanceClient binanceClient;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wireMock) {
        BinanceProperties properties = new BinanceProperties(
                wireMock.getHttpBaseUrl(),
                AVG_PRICE_PATH,
                SYMBOL,
                Duration.ofSeconds(1),
                Duration.ofSeconds(2),
                Duration.ofSeconds(30));
        RestClient restClient = new BinanceClientConfig().binanceRestClient(RestClient.builder(), properties);
        binanceClient = new BinanceClient(properties, restClient);
    }

    @Test
    void returnsParsedResponseWhenBinanceRespondsOk() {
        stubFor(get(urlPathEqualTo(AVG_PRICE_PATH))
                .willReturn(okJson("""
                        {"mins":5,"price":"612.34567890","closeTime":1694061154503}
                        """)));

        BinanceAvgPriceResponse response = binanceClient.getAveragePrice(SYMBOL);

        assertThat(response.mins()).isEqualTo(5);
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("612.34567890"));
        assertThat(response.closeTime()).isEqualTo(1694061154503L);
        verify(getRequestedFor(urlPathEqualTo(AVG_PRICE_PATH))
                .withQueryParam("symbol", equalTo(SYMBOL)));
    }

    @Test
    void throwsWithBinanceErrorDetailsOnBadRequest() {
        stubFor(get(urlPathEqualTo(AVG_PRICE_PATH))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"code":-1121,"msg":"Invalid symbol."}
                                """)));

        assertThatThrownBy(() -> binanceClient.getAveragePrice("WRONG"))
                .isInstanceOf(BinanceClientException.class)
                .hasMessageContaining("400")
                .hasMessageContaining("Invalid symbol.");
    }

    @ParameterizedTest
    @ValueSource(ints = {418, 429, 500, 503})
    void throwsOnRateLimitAndServerErrors(int status) {
        stubFor(get(urlPathEqualTo(AVG_PRICE_PATH))
                .willReturn(aResponse().withStatus(status)));

        assertThatThrownBy(() -> binanceClient.getAveragePrice(SYMBOL))
                .isInstanceOf(BinanceClientException.class)
                .hasMessageContaining(String.valueOf(status));
    }

    @Test
    void throwsWhenBinanceRespondsSlowerThanReadTimeout() {
        stubFor(get(urlPathEqualTo(AVG_PRICE_PATH))
                .willReturn(okJson("""
                        {"mins":5,"price":"612.34567890","closeTime":1694061154503}
                        """).withFixedDelay(4000)));

        assertThatThrownBy(() -> binanceClient.getAveragePrice(SYMBOL))
                .isInstanceOf(BinanceClientException.class);
    }

    @Test
    void throwsWhenResponseHasNoPrice() {
        stubFor(get(urlPathEqualTo(AVG_PRICE_PATH))
                .willReturn(okJson("{}")));

        assertThatThrownBy(() -> binanceClient.getAveragePrice(SYMBOL))
                .isInstanceOf(BinanceClientException.class)
                .hasMessageContaining("invalid response");
    }
}
