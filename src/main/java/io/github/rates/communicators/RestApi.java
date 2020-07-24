package io.github.rates.communicators;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface RestApi<T> {

    CompletableFuture<HttpResponse<T>> sendAsyncGetRequest(HttpRequest request);

}
