package io.github.rates.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.rates.model.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

class ExchangeRatesCacheTest {

    @Test
    void updateCryptoCurrenciesRates() {
        Rate rate = new Rate("test", BigDecimal.TEN);
        ExchangeRatesCache cache = new ExchangeRatesCache();

        cache.updateCryptoCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(rate.getPairName()));
    }

    @Test
    void updateCryptoCurrenciesRates_updateExisted() {
        Rate oldRate = new Rate("test", BigDecimal.ZERO);
        Rate newRate = new Rate("test", BigDecimal.ONE);
        ExchangeRatesCache cache = new ExchangeRatesCache();

        cache.updateCryptoCurrenciesRates(List.of(oldRate));
        cache.updateCryptoCurrenciesRates(List.of(newRate));

        assertEquals(newRate, cache.getCryptoCurrencyRate(oldRate.getPairName()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BTCETH", "BTCeth", "btcETH", "btceth"})
    void getCryptoCurrencyRate(String pairName) {
        Rate rate = new Rate(pairName.toLowerCase(), BigDecimal.valueOf(23942.234));
        ExchangeRatesCache cache = new ExchangeRatesCache();

        cache.updateCryptoCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(pairName));
    }


    @Test
    void getCryptoCurrencyRate_notFound() {
        ExchangeRatesCache cache = new ExchangeRatesCache();

        assertNull(cache.getCryptoCurrencyRate("test"));
    }

    @Test
    void clear() {
        Rate rate = new Rate("test", BigDecimal.TEN);
        ExchangeRatesCache cache = new ExchangeRatesCache();

        cache.updateCryptoCurrenciesRates(List.of(rate));

        assertEquals(rate, cache.getCryptoCurrencyRate(rate.getPairName()));

        cache.clear();

        assertNull(cache.getCryptoCurrencyRate(rate.getPairName()));
    }

}
