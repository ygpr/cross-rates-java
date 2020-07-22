package io.github.rates.parsers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.rates.model.Rate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class JavascriptEngineRatesJsonParserTest {

    private final RatesJsonParser jsonParser = ParsersFactory.createRatesJsonParser();

    @Test
    void parseJson() {
        BigDecimal price = BigDecimal.ONE;
        String pairName = "btceth";
        String json = "[{\"symbol\":\"" + pairName + "\",\"price\":\"" + price + "\"}]";

        List<Rate> rates = jsonParser.parseJson(json);

        assertEquals(1, rates.size());
        assertEquals(pairName.toUpperCase(), rates.get(0).getPairName());
        assertThat(price, Matchers.comparesEqualTo(rates.get(0).getPrice()));
    }

    @Test
    void parseJson_shouldThrowRuntimeException() {
        String json = "{\"symbol\":\"test\",\"price\":\"1000\"}";
        assertThrows(RuntimeException.class, () -> jsonParser.parseJson(json));
    }
}
