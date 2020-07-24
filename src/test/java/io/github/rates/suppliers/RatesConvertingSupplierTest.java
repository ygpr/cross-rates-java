package io.github.rates.suppliers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.domain.Rate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

class RatesConvertingSupplierTest {

    private final String asset = "BTC";
    private final String quotable = "ETH";
    private final Rate rate = new Rate(asset, quotable, asset + quotable, 1, 1, BigDecimal.ONE);
    private final CurrencyRatesCache cache = new CurrencyRatesCache();
    private final RatesConvertingSupplier ratesConvertingSupplier = new RatesConvertingSupplier(cache, Executors.newSingleThreadScheduledExecutor());

    @BeforeEach
    void setUp() {
        cache.clear();
        cache.updateCryptoCurrenciesRates(List.of(rate));
    }

    @Test
    void convert() {
        BigDecimal amount = BigDecimal.valueOf(3.001);
        BigDecimal expected = amount.multiply(rate.getPrice());
        Optional<BigDecimal> convertedOpt = ratesConvertingSupplier.convert(amount, asset, quotable);
        assertTrue(convertedOpt.isPresent());
        assertThat(expected, Matchers.comparesEqualTo(convertedOpt.get()));
    }

    @Test
    void convert_emptyResponse() {
        assertTrue(ratesConvertingSupplier.convert(BigDecimal.ONE, "a", "b").isEmpty());
    }

    @Test
    void convertAsynchronously() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(1226.92);
        BigDecimal expected = amount.multiply(rate.getPrice());
        assertThat(expected, Matchers.comparesEqualTo(ratesConvertingSupplier.convertAsynchronously(amount, asset, quotable).get()));
    }

    @Test
    void convertAsynchronously_emptyResponse() throws Exception {
        assertNull(ratesConvertingSupplier.convertAsynchronously(BigDecimal.ONE, "a", "b").get());
    }

    @Test
    void convertAsynchronouslyDelayed() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(4);
        BigDecimal expected = amount.multiply(rate.getPrice());
        assertThat(expected, Matchers.comparesEqualTo(ratesConvertingSupplier.convertAsynchronously(amount, asset, quotable, 1).get()));
    }

    @Test
    void convertAsynchronouslyDelayed_emptyResponse() throws Exception {
        assertNull(ratesConvertingSupplier.convertAsynchronously(BigDecimal.ONE, "a", "b", 1).get());
    }

    @Test
    void getRate() {
        Optional<Rate> rateOpt = ratesConvertingSupplier.getRate(asset, quotable);
        assertTrue(rateOpt.isPresent());
        assertEquals(rate, rateOpt.get());
    }

    @Test
    void getRate_emptyResponse() {
        assertTrue(ratesConvertingSupplier.getRate("a", "b").isEmpty());
    }

    @Test
    void getRateAsynchronously() throws Exception {
        assertEquals(rate, ratesConvertingSupplier.getRateAsynchronously(asset, quotable).get());
    }

    @Test
    void getRateAsynchronously_emptyResponse() throws Exception {
        assertNull(ratesConvertingSupplier.getRateAsynchronously("a", "b").get());
    }

    @Test
    void getRateAsynchronouslyDelayed() throws Exception {
        assertEquals(rate, ratesConvertingSupplier.getRateAsynchronously(asset, quotable, 1).get());
    }

    @Test
    void getRateAsynchronouslyDelayed_emptyResponse() throws Exception {
        assertNull(ratesConvertingSupplier.getRateAsynchronously("a", "b", 1).get());
    }

}
