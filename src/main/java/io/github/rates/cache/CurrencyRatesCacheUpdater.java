package io.github.rates.cache;

import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.suppliers.TargetRatesProvider;

public class CurrencyRatesCacheUpdater {

    private final CurrencyRatesCache currencyRatesCache;
    private final TargetRatesProvider targetRatesProvider;
    private final CacheUpdateProgram cacheUpdateProgram;

    public CurrencyRatesCacheUpdater(
            CurrencyRatesCache currencyRatesCache,
            TargetRatesProvider targetRatesProvider,
            CacheUpdateProgram cacheUpdateProgram
    ) {
        this.currencyRatesCache = currencyRatesCache;
        this.targetRatesProvider = targetRatesProvider;
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
        return () -> targetRatesProvider.getRatesFromTarget()
                .thenAccept(currencyRatesCache::updateCurrenciesRates);
    }
}
