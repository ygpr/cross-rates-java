package io.github.rates.suppliers;

import static org.junit.jupiter.api.Assertions.*;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.domain.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class CachedRatesSupplierTest {

    private final String asset = "BTC";
    private final String quotable = "ETH";
    private final Rate rate = new Rate(asset, quotable, asset + quotable, 1, 1, BigDecimal.ONE);
    private final CurrencyRatesCache cache = new CurrencyRatesCache();
    private final CachedRatesSupplier cachedRatesSupplier = new CachedRatesSupplier(cache, Executors.newSingleThreadScheduledExecutor());

    @BeforeEach
    void setUp() {
        cache.clear();
        cache.updateCurrenciesRates(List.of(rate));
    }

    @Test
    void getRate() {
        Optional<Rate> rateOpt = cachedRatesSupplier.getRate(asset, quotable);
        assertTrue(rateOpt.isPresent());
        assertEquals(rate, rateOpt.get());
    }

    @Test
    void getRate_emptyResponse() {
        assertTrue(cachedRatesSupplier.getRate("a", "b").isEmpty());
    }

    @Test
    void getRateAsynchronously() throws Exception {
        CountDownLatch count = new CountDownLatch(1);
        cachedRatesSupplier.getRateAsynchronously(asset, quotable).thenRunAsync(count::countDown);
        count.await(5, TimeUnit.SECONDS);
        assertEquals(rate, cachedRatesSupplier
                .getRateAsynchronously(asset, quotable).get(5, TimeUnit.SECONDS));
    }

    @Test
    void getRateAsynchronously_incompleteFutureExpected() {
        assertThrows(
                TimeoutException.class,
                () -> cachedRatesSupplier.getRateAsynchronously("a", "b")
                        .get(5, TimeUnit.SECONDS)
        );
    }

    @Test
    void getRateAsynchronouslyDelayed() throws Exception {
        assertEquals(rate, cachedRatesSupplier.getRateAsynchronously(asset, quotable, 1)
                .get(5, TimeUnit.SECONDS));
    }

    @Test
    void getRateAsynchronouslyDelayed_emptyResponse() throws Exception {
        assertNull(cachedRatesSupplier.getRateAsynchronously("a", "b", 1)
                .get(5, TimeUnit.SECONDS));
    }

}
