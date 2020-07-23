package io.github.rates.cache;

import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.suppliers.TargetRatesSupplier;

public class CacheUpdater {

    private final ExchangeRatesCache exchangeRatesCache;
    private final TargetRatesSupplier targetRatesSupplier;
    private final CacheUpdateProgram cacheUpdateProgram;

    public CacheUpdater(
            ExchangeRatesCache exchangeRatesCache,
            TargetRatesSupplier targetRatesSupplier,
            CacheUpdateProgram cacheUpdateProgram
    ) {
        this.exchangeRatesCache = exchangeRatesCache;
        this.targetRatesSupplier = targetRatesSupplier;
        this.cacheUpdateProgram = cacheUpdateProgram;
    }

    public void startProgram() {
        cacheUpdateProgram.getExecutorService().scheduleWithFixedDelay(
                getRatesAndCallUpdate(),
                cacheUpdateProgram.getInitialDelay(),
                cacheUpdateProgram.getDelay(),
                cacheUpdateProgram.getTimeUnit()
        );
    }

    private Runnable getRatesAndCallUpdate() {
        return () -> targetRatesSupplier.getRatesFromTarget().thenAccept(exchangeRatesCache::updateCryptoCurrenciesRates);
    }
}
