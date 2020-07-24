package io.github.rates.communicators;

import io.github.rates.tools.json.UncheckedObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class AbstractRestRestApi implements RestApi<String> {

    protected final HttpClient httpClient;
    protected final UncheckedObjectMapper objectMapper;

    public AbstractRestRestApi(HttpClient httpClient, UncheckedObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<HttpResponse<String>> sendAsyncGetRequest(HttpRequest request) {
        try {
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Exception during sending GET request: %s", request), e);
        }
    }

    protected HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
    }

}
