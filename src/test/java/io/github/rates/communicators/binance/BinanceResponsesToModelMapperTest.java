package io.github.rates.communicators.binance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.rates.communicators.binance.model.response.BinanceRateResponse;
import io.github.rates.communicators.binance.model.response.SymbolResponse;
import io.github.rates.domain.Rate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class BinanceResponsesToModelMapperTest {

    private final BinanceResponsesToModelMapper modelMapper = new BinanceResponsesToModelMapper();

    @Test
    void mapToRate() {
        String asset = "ADA";
        String quotable = "ZEC";
        BigDecimal price = BigDecimal.valueOf(234.010563);

        SymbolResponse symbolResponse = new SymbolResponse(asset + quotable, asset, quotable);
        BinanceRateResponse rateResponse = new BinanceRateResponse(asset + quotable, price);
        Rate expectedRate = new Rate(asset, quotable, price);

        assertEquals(List.of(expectedRate), modelMapper.mapToRate(List.of(rateResponse), List.of(symbolResponse)));
    }

    @Test
    void mapToRate_symbolOfRateNotFound_shouldSkip() {
        SymbolResponse symbolResponse = new SymbolResponse("TEST", "TE", "ST");
        BinanceRateResponse rateResponse = new BinanceRateResponse("BTCETH", BigDecimal.ONE);

        assertTrue(modelMapper.mapToRate(List.of(rateResponse), List.of(symbolResponse)).isEmpty());
    }
}
