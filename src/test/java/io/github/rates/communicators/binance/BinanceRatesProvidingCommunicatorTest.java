package io.github.rates.communicators.binance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.binance.model.response.ExchangeInfoResponse;
import io.github.rates.communicators.binance.model.response.RateResponse;
import io.github.rates.communicators.binance.model.response.SymbolResponse;
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
class BinanceRatesProvidingCommunicatorTest {

    @Mock
    private BinanceApi binanceApi;

    @Mock
    private BinanceResponsesToModelMapper binanceResponsesToModelMapper;

    @InjectMocks
    private BinanceRatesProvidingCommunicator binanceRatesJsonSupplier;

    @Test
    void getRates() throws Exception {
        String asset = "BTC";
        String quotable = "ETH";
        BigDecimal price = BigDecimal.valueOf(45.992111);

        SymbolResponse symbolResponse = new SymbolResponse(asset + quotable, asset, quotable);
        RateResponse rateResponse = new RateResponse(asset + quotable, price);
        Rate rate = new Rate(asset, quotable, price);

        given(binanceApi.sendRequestForRates()).willReturn(CompletableFuture.completedFuture(List.of(rateResponse)));
        given(binanceApi.sendRequestForExchangeInfo()).willReturn(CompletableFuture.completedFuture(new ExchangeInfoResponse(List.of(symbolResponse))));
        given(binanceResponsesToModelMapper.mapToRate(List.of(rateResponse), List.of(symbolResponse))).willReturn(List.of(rate));

        assertEquals(List.of(rate), binanceRatesJsonSupplier.getRates().get());
    }

    @Test
    void getRates_exception_shouldRethrowRuntime() {
        given(binanceApi.sendRequestForRates()).willAnswer(invocation -> {
            throw new IOException();
        });

        assertThrows(RuntimeException.class, () -> binanceRatesJsonSupplier.getRates());
    }
}
