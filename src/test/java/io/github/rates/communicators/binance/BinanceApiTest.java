package io.github.rates.communicators.binance;

import static io.github.rates.communicators.binance.BinanceEndpoints.RATES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

@ExtendWith(MockitoExtension.class)
class BinanceApiTest {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private BinanceApi binanceApi;

    @Test
    void sendRequestForRates() throws Exception {
        String response = "test response";

        HttpResponse<Object> mockedHttpResponse = Mockito.mock(HttpResponse.class);

        given(mockedHttpResponse.body()).willReturn(response);
        given(httpClient.send(
                argThat(request -> request.method().equals("GET") && request.uri().equals(URI.create(RATES))), any()))
                .willReturn(mockedHttpResponse);

        assertEquals(response, binanceApi.sendRequestForRates());
    }
}
