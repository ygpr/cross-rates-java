package io.github.rates.cache;

import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.suppliers.TargetRatesSupplier;

public class CryptoCurrencyRatesCacheUpdater {

    private final CurrencyRatesCache currencyRatesCache;
    private final TargetRatesSupplier targetRatesSupplier;
    private final CacheUpdateProgram cacheUpdateProgram;

    public CryptoCurrencyRatesCacheUpdater(
            CurrencyRatesCache currencyRatesCache,
            TargetRatesSupplier targetRatesSupplier,
            CacheUpdateProgram cacheUpdateProgram
    ) {
        this.currencyRatesCache = currencyRatesCache;
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
        return () -> targetRatesSupplier.getRatesFromTarget().thenAccept(currencyRatesCache::updateCryptoCurrenciesRates);
    }
}
