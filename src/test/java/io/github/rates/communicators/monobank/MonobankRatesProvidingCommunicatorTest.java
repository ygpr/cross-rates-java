package io.github.rates.communicators.monobank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import io.github.rates.domain.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class MonobankRatesProvidingCommunicatorTest {

    @Mock
    private MonobankRestApi monobankApi;

    @Mock
    private MonobankResponseToModelMapper responseToModelMapper;

    @InjectMocks
    private MonobankRatesProvidingCommunicator ratesProvidingCommunicator;

    @Test
    void getRates() throws Exception {
        String asset = "EUR";
        String quotable = "USD";
        BigDecimal price = BigDecimal.valueOf(45.992111);
        Rate rate = new Rate(asset, quotable, asset + quotable, 4, 4, price);
        MonobankRateResponse monobankRateResponse = new MonobankRateResponse(24234, 335435, price, price, price, System.currentTimeMillis());

        given(monobankApi.sendRequestForRates()).willReturn(CompletableFuture.completedFuture(List.of(monobankRateResponse)));
        given(responseToModelMapper.mapToRate(List.of(monobankRateResponse))).willReturn(List.of(rate));

        assertEquals(List.of(rate), ratesProvidingCommunicator.getRates().get());
    }

    @Test
    void getRates_exception_shouldRethrowRuntime() {
        given(monobankApi.sendRequestForRates()).willAnswer(invocation -> {
            throw new IOException();
        });

        assertThrows(RuntimeException.class, () -> ratesProvidingCommunicator.getRates());
    }
}
