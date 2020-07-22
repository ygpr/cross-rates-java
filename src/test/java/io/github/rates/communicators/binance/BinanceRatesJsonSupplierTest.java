package io.github.rates.communicators.binance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class BinanceRatesJsonSupplierTest {

    @Mock
    private BinanceApi binanceApi;

    @InjectMocks
    private BinanceRatesJsonSupplier binanceRatesJsonSupplier;

    @Test
    void getRatesAsJson() throws Exception {
        String json = "{\"method\":\"test\"}";

        given(binanceApi.sendRequestForRates()).willReturn(json);

        assertEquals(json, binanceRatesJsonSupplier.getRatesAsJson());
    }

    @Test
    void getRatesAsJson_exception_shouldRethrowRuntime() throws Exception {
        given(binanceApi.sendRequestForRates()).willThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> binanceRatesJsonSupplier.getRatesAsJson());
    }
}
