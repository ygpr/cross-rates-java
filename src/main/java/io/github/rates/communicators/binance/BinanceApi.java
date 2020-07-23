package io.github.rates.communicators.binance;

import static io.github.rates.communicators.binance.BinanceEndpoints.EXCHANGE_INFO;
import static io.github.rates.communicators.binance.BinanceEndpoints.RATES;

import io.github.rates.communicators.binance.model.response.ExchangeInfoResponse;
import io.github.rates.communicators.binance.model.response.RateResponse;
import io.github.rates.tools.json.UncheckedObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class BinanceApi {

    private final HttpClient httpClient;
    private final UncheckedObjectMapper objectMapper;

    BinanceApi(HttpClient httpClient, UncheckedObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    CompletableFuture<List<RateResponse>> sendRequestForRates() {
        return sendAsyncGetRequest(RATES)
                .thenApply(HttpResponse::body)
                .thenApply(json -> objectMapper.readValueAsList(json, RateResponse.class));
    }

    CompletableFuture<ExchangeInfoResponse> sendRequestForExchangeInfo() {
        return sendAsyncGetRequest(EXCHANGE_INFO)
                .thenApply(HttpResponse::body)
                .thenApply(json -> objectMapper.readValue(json, ExchangeInfoResponse.class));
    }

    private CompletableFuture<HttpResponse<String>> sendAsyncGetRequest(String url) {
        try {
            return httpClient.sendAsync(createGetRequest(url), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Exception during sending GET request to resource: %s", url), e);
        }
    }

    private HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
    }

}
