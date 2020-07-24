package io.github.rates.tools.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.github.rates.communicators.binance.model.response.BinanceRateResponse;
import io.github.rates.communicators.binance.model.response.SymbolResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class UncheckedObjectMapperTest {

    private final UncheckedObjectMapper uncheckedObjectMapper = new UncheckedObjectMapper();

    @Test
    void readValue() {
        String asset = "LTC";
        String quotable = "USDT";
        String json = "{\"symbol\":\"" + asset + quotable + "\",\"baseAsset\":\"" + asset + "\",\"quoteAsset\":\"" + quotable + "\"}";

        SymbolResponse symbolResponse = uncheckedObjectMapper.readValue(json, SymbolResponse.class);

        assertNotNull(symbolResponse);
        assertEquals(asset, symbolResponse.getBaseAsset());
        assertEquals(quotable, symbolResponse.getQuoteAsset());
        assertEquals(asset + quotable, symbolResponse.getSymbol());
        assertEquals(asset + quotable, symbolResponse.getPairName());
    }

    @Test
    void readValue_readException_shouldThrowRuntimeException() {
        String json = "[{\"symbol\":\"btceth\",\"baseAsset\":\"btc\",\"quoteAsset\":\"eth\"}]";

        assertThrows(RuntimeException.class, () -> uncheckedObjectMapper.readValue(json, SymbolResponse.class));
    }

    @Test
    void readValueAsList() {
        String symbol = "DASHETH";
        BigDecimal price = BigDecimal.valueOf(500.009);

        String json = "[{\"symbol\":\"" + symbol + "\",\"price\":\"" + price + "\"}]";

        List<BinanceRateResponse> rateResponses = uncheckedObjectMapper.readValueAsList(json, BinanceRateResponse.class);

        assertNotNull(rateResponses);
        assertFalse(rateResponses.isEmpty());
        assertEquals(symbol, rateResponses.get(0).getSymbol());
        assertEquals(symbol, rateResponses.get(0).getPairName());
        assertThat(price, Matchers.comparesEqualTo(rateResponses.get(0).getPrice()));
    }


    @Test
    void readValueAsList_readException_shouldThrowRuntimeException() {
        String json = "{\"symbol\":\"IDDQD\",\"price\":\"244\"}";

        assertThrows(RuntimeException.class, () -> uncheckedObjectMapper.readValueAsList(json, BinanceRateResponse.class));
    }

}
