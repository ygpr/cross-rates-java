package io.github.rates;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.cache.CurrencyRatesCacheUpdater;
import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.impl.DefaultCrossRatesAPI;
import io.github.rates.suppliers.BinanceTargetRatesProvider;
import io.github.rates.suppliers.CachedRatesSupplier;
import io.github.rates.suppliers.MonobankTargetRatesProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrossRatesApiFactory {

    public CrossRatesAPI buildDefault() {
        ScheduledExecutorService executorService = getDefaultScheduledExecutorService();
        return build(
                executorService,
                new CacheUpdateProgram(executorService, 0L, 1L, TimeUnit.MINUTES)
        );
    }

    private ScheduledExecutorService getDefaultScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public CrossRatesAPI build(ScheduledExecutorService executorService, CacheUpdateProgram program) {
        CurrencyRatesCache cache = new CurrencyRatesCache();
        new CurrencyRatesCacheUpdater(
                cache, new BinanceTargetRatesProvider(), program
        ).startProgram();
        new CurrencyRatesCacheUpdater(
                cache, new MonobankTargetRatesProvider(), program
        ).startProgram();
        return new DefaultCrossRatesAPI(new CachedRatesSupplier(cache, executorService));
    }

    public CrossRatesAPI build(CacheUpdateProgram program) {
        CurrencyRatesCache cache = new CurrencyRatesCache();
        new CurrencyRatesCacheUpdater(
                cache, new BinanceTargetRatesProvider(), program
        ).startProgram();
        new CurrencyRatesCacheUpdater(
                cache, new MonobankTargetRatesProvider(), program
        ).startProgram();
        return new DefaultCrossRatesAPI(new CachedRatesSupplier(cache, getDefaultScheduledExecutorService()));
    }

}
