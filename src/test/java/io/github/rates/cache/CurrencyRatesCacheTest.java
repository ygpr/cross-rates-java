package io.github.rates.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.rates.domain.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

class CurrencyRatesCacheTest {

    @Test
    void updateCryptoCurrenciesRates() {
        Rate rate = new Rate("test", "test", "test", 4, 5, BigDecimal.TEN);
        CurrencyRatesCache cache = new CurrencyRatesCache();

        cache.updateCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(rate.getPairName()));
    }

    @Test
    void updateCryptoCurrenciesRates_updateExisted() {
        Rate oldRate = new Rate("test", "test", "test", 5, 5, BigDecimal.ZERO);
        Rate newRate = new Rate("test", "test", "test", 1, 1, BigDecimal.ONE);
        CurrencyRatesCache cache = new CurrencyRatesCache();

        cache.updateCurrenciesRates(List.of(oldRate));
        cache.updateCurrenciesRates(List.of(newRate));

        assertEquals(newRate, cache.getCryptoCurrencyRate(oldRate.getPairName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BTCETH", "BTCeth", "btcETH", "btceth"})
    void getCryptoCurrencyRate(String pairName) {
        Rate rate = new Rate("btc", "eth", "btceth", 5, 6, BigDecimal.valueOf(23942.234));
        CurrencyRatesCache cache = new CurrencyRatesCache();

        cache.updateCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(pairName));
    }


    @Test
    void getCryptoCurrencyRate_notFound() {
        CurrencyRatesCache cache = new CurrencyRatesCache();

        assertNull(cache.getCryptoCurrencyRate("test"));
    }

    @Test
    void clear() {
        Rate rate = new Rate("test", "test", "test", 7, 7, BigDecimal.TEN);
        CurrencyRatesCache cache = new CurrencyRatesCache();

        cache.updateCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(rate.getPairName()));

        cache.clear();

        assertNull(cache.getCryptoCurrencyRate(rate.getPairName()));
    }

}
