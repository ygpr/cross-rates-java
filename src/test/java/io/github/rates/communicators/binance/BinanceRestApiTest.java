package io.github.rates.communicators.binance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.binance.model.response.BinanceRateResponse;
import io.github.rates.communicators.binance.model.response.ExchangeInfoResponse;
import io.github.rates.communicators.binance.model.response.SymbolResponse;
import io.github.rates.tools.json.UncheckedObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class BinanceRestApiTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private UncheckedObjectMapper uncheckedObjectMapper;

    @InjectMocks
    private BinanceRestApi binanceApi;

    @Test
    void sendRequestForRates() throws Exception {
        String json = "[{\"symbol\":\"btceth\",\"price\":\"1\"}]";
        BinanceRateResponse rateResponse = new BinanceRateResponse("btceth", BigDecimal.ONE);

        HttpResponse<Object> mockedHttpResponse = Mockito.mock(HttpResponse.class);

        given(mockedHttpResponse.body()).willReturn(json);
        given(uncheckedObjectMapper.readValueAsList(json, BinanceRateResponse.class)).willReturn(List.of(rateResponse));
        given(httpClient.sendAsync(
                argThat(request -> request.method().equals("GET") && request.uri().equals(URI.create(BinanceEndpoints.RATES))), any()))
                .willReturn(CompletableFuture.completedFuture(mockedHttpResponse));

        assertEquals(List.of(rateResponse), binanceApi.sendRequestForRates().get());
    }

    @Test
    void sendRequestForSymbols() throws Exception {
        String json = "[{\"symbol\":\"btceth\",\"baseAsset\":\"btc\",\"quoteAsset\":\"eth\",\"baseAssetPrecision\":\"8\",\"quotePrecision\":\"8\"}]";
        SymbolResponse symbolResponse = new SymbolResponse("btceth", "btc", "eth", 8, 8);
        ExchangeInfoResponse exchangeInfoResponse = new ExchangeInfoResponse(List.of(symbolResponse));

        HttpResponse<Object> mockedHttpResponse = Mockito.mock(HttpResponse.class);
        given(mockedHttpResponse.body()).willReturn(json);
        given(uncheckedObjectMapper.readValue(json, ExchangeInfoResponse.class)).willReturn(exchangeInfoResponse);
        given(httpClient.sendAsync(
                argThat(request -> request.method().equals("GET") && request.uri().equals(URI.create(BinanceEndpoints.EXCHANGE_INFO))), any()))
                .willReturn(CompletableFuture.completedFuture(mockedHttpResponse));

        assertEquals(exchangeInfoResponse, binanceApi.sendRequestForExchangeInfo().get());
    }
}
