package io.github.rates.communicators.monobank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
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
class MonobankRestApiTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private UncheckedObjectMapper uncheckedObjectMapper;

    @InjectMocks
    private MonobankRestApi monobankApi;

    @Test
    void sendRequestForRates() throws Exception {
        String json = "json";
        MonobankRateResponse rateResponse = new MonobankRateResponse(1, 2, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, System.currentTimeMillis());

        HttpResponse<Object> mockedHttpResponse = Mockito.mock(HttpResponse.class);
        given(mockedHttpResponse.body()).willReturn(json);
        given(uncheckedObjectMapper.readValueAsList(json, MonobankRateResponse.class)).willReturn(List.of(rateResponse));
        given(httpClient.sendAsync(
                argThat(request -> request.method().equals("GET") && request.uri().equals(URI.create(MonobankEndpoints.RATES))), any()))
                .willReturn(CompletableFuture.completedFuture(mockedHttpResponse));

        assertEquals(List.of(rateResponse), monobankApi.sendRequestForRates().get());
    }

}
