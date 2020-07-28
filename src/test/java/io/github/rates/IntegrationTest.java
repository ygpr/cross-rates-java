package io.github.rates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.cache.CurrencyRatesCacheUpdater;
import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.domain.Rate;
import io.github.rates.suppliers.BinanceTargetRatesProvider;
import io.github.rates.suppliers.CachedRatesSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class IntegrationTest {

    @Mock
    private BinanceTargetRatesProvider binanceTargetRatesSupplier;

    @Test
    void allFlowTest() throws Exception {
        String asset = "ADA";
        String quotable = "BTC";
        Rate rate = new Rate(
                asset, quotable, asset + quotable, 8, 8, BigDecimal.valueOf(4550.9311)
        );

        CurrencyRatesCache cache = new CurrencyRatesCache();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        CachedRatesSupplier cachedRatesSupplier = new CachedRatesSupplier(cache, executorService);
        CacheUpdateProgram cacheUpdateProgram = new CacheUpdateProgram(
                executorService, 0L, 1L, TimeUnit.MINUTES
        );
        CurrencyRatesCacheUpdater cryptoCurrencyRatesCacheUpdater = new CurrencyRatesCacheUpdater(
                cache, binanceTargetRatesSupplier, cacheUpdateProgram
        );
        given(binanceTargetRatesSupplier.getRatesFromTarget()).willReturn(CompletableFuture.completedFuture(List.of(rate)));

        cryptoCurrencyRatesCacheUpdater.startProgram();

        assertEquals(rate, cachedRatesSupplier.getRateAsynchronously(asset, quotable, 2).get());
        assertEquals(rate, cachedRatesSupplier.getRateAsynchronously(asset, quotable).get());
        assertEquals(rate, cachedRatesSupplier.getRate(asset, quotable).orElseThrow());
    }

}
