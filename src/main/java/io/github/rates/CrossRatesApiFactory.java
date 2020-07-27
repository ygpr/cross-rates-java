package io.github.rates;

import io.github.rates.cache.CurrencyRatesCacheUpdater;
import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.suppliers.BinanceTargetRatesSupplier;
import io.github.rates.suppliers.MonobankTargetRatesSupplier;
import io.github.rates.suppliers.RateConverterSupplier;
import io.github.rates.suppliers.RatesConvertingSupplier;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CrossRatesApiFactory {

    public RateConverterSupplier buildDefault() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        return build(
                executorService,
                new CacheUpdateProgram(executorService, 0L, 1L, TimeUnit.MINUTES)
        );
    }

    public RateConverterSupplier build(ScheduledExecutorService executorService, CacheUpdateProgram program) {
        CurrencyRatesCache cache = new CurrencyRatesCache();
        new CurrencyRatesCacheUpdater(
                cache, new BinanceTargetRatesSupplier(), program
        ).startProgram();
        new CurrencyRatesCacheUpdater(
                cache, new MonobankTargetRatesSupplier(), program
        ).startProgram();
        return new RatesConvertingSupplier(cache, executorService);
    }

}
