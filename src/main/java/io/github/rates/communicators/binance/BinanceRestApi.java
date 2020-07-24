package io.github.rates.communicators.binance;

import io.github.rates.communicators.AbstractRestRestApi;
import io.github.rates.communicators.binance.model.response.BinanceRateResponse;
import io.github.rates.communicators.binance.model.response.ExchangeInfoResponse;
import io.github.rates.tools.json.UncheckedObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class BinanceRestApi extends AbstractRestRestApi {

    BinanceRestApi(HttpClient httpClient, UncheckedObjectMapper objectMapper) {
        super(httpClient, objectMapper);
    }

    CompletableFuture<List<BinanceRateResponse>> sendRequestForRates() {
        return sendAsyncGetRequest(createGetRequest(BinanceEndpoints.RATES))
                .thenApply(HttpResponse::body)
                .thenApply(json -> objectMapper.readValueAsList(json, BinanceRateResponse.class));
    }

    CompletableFuture<ExchangeInfoResponse> sendRequestForExchangeInfo() {
        return sendAsyncGetRequest(createGetRequest(BinanceEndpoints.EXCHANGE_INFO))
                .thenApply(HttpResponse::body)
                .thenApply(json -> objectMapper.readValue(json, ExchangeInfoResponse.class));
    }

}
