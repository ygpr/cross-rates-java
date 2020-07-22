package io.github.rates.communicators.binance;

import static io.github.rates.communicators.binance.BinanceEndpoints.RATES;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

class BinanceApi {

    private final HttpClient httpClient;

    BinanceApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    String sendRequestForRates() throws IOException, InterruptedException {
        return httpClient
                .send(createGetRequest(RATES), BodyHandlers.ofString())
                .body();
    }

    private HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
    }

}
