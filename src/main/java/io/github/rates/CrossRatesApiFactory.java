package io.github.rates;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.cache.CurrencyRatesCacheUpdater;
import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.suppliers.BinanceTargetRatesProvider;
import io.github.rates.suppliers.CachedRatesSupplier;
import io.github.rates.suppliers.MonobankTargetRatesProvider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrossRatesApiFactory {

    public CrossRatesAPI buildDefault() {
        ScheduledExecutorService executorService = getDefaultScheduledExecutorService();
        return build(executorService, getDefaultProgram(executorService));
    }

    private CacheUpdateProgram getDefaultProgram(ScheduledExecutorService executorService) {
        return new CacheUpdateProgram(executorService, 0L, 1L, TimeUnit.MINUTES);
    }

    private ScheduledExecutorService getDefaultScheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    public CrossRatesAPI build(ScheduledExecutorService executorService) {
        return build(executorService, getDefaultProgram(executorService));
    }

    public CrossRatesAPI build(CacheUpdateProgram program) {
        return build(getDefaultScheduledExecutorService(), program);
    }

    public CrossRatesAPI build(ScheduledExecutorService executorService, CacheUpdateProgram program) {
        return build(executorService, program, program);
    }

    public CrossRatesAPI build(CacheUpdateProgram fiatProgram, CacheUpdateProgram cryptoProgram) {
        return build(getDefaultScheduledExecutorService(), fiatProgram, cryptoProgram);
    }

    public CrossRatesAPI build(
            ScheduledExecutorService executorService,
            CacheUpdateProgram fiatProgram,
            CacheUpdateProgram cryptoProgram
    ) {
        CurrencyRatesCache cache = new CurrencyRatesCache();
        new CurrencyRatesCacheUpdater(
                cache, new BinanceTargetRatesProvider(), cryptoProgram
        ).startProgram();
        new CurrencyRatesCacheUpdater(
                cache, new MonobankTargetRatesProvider(), fiatProgram
        ).startProgram();
        return new DefaultCrossRatesAPI(new CachedRatesSupplier(cache, executorService));
    }

}
