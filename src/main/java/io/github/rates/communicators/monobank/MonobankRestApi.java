package io.github.rates.communicators.monobank;

import io.github.rates.communicators.RestApi;
import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import io.github.rates.tools.json.UncheckedObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

class MonobankRestApi extends RestApi {

    MonobankRestApi(HttpClient httpClient, UncheckedObjectMapper objectMapper) {
        super(httpClient, objectMapper);
    }

    CompletableFuture<List<MonobankRateResponse>> sendRequestForRates() {
        return sendAsyncGetRequest(createGetRequest(MonobankEndpoints.RATES))
                .thenApply(HttpResponse::body)
                .thenApply(json -> objectMapper.readValueAsList(json, MonobankRateResponse.class));
    }

}
