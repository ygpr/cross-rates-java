package io.github.rates.suppliers;

import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class CachedRatesSupplier implements RatesSupplier {

    final CurrencyRatesCache ratesCache;
    final ScheduledExecutorService executorService;

    public CachedRatesSupplier(CurrencyRatesCache ratesCache, ScheduledExecutorService executorService) {
        this.ratesCache = ratesCache;
        this.executorService = executorService;
    }

    @Override
    public Optional<Rate> getRate(String asset, String quotable) {
        return Optional.ofNullable(ratesCache.getCryptoCurrencyRate(asset + quotable));
    }

    @Override
    public CompletableFuture<Rate> getRateAsynchronously(String asset, String quotable) {
        return ratesCache.getCryptoCurrencyRateAsync(asset + quotable);
    }


    @Override
    public CompletableFuture<Optional<Rate>> getRateAsync(String asset, String quotable) {
        return ratesCache.findCryptoCurrencyRateAsync(asset + quotable);
    }

    @Override
    public CompletableFuture<Rate> getRateAsynchronously(String asset, String quotable, long delayInSeconds) {
        return CompletableFuture.supplyAsync(
                () -> ratesCache.getCryptoCurrencyRate(asset + quotable),
                delayedExecutor(delayInSeconds, TimeUnit.SECONDS)
        );
    }

    private Executor delayedExecutor(long delayInSeconds, TimeUnit unit) {
        return delayedExecutor(delayInSeconds, unit, ForkJoinPool.commonPool());
    }

    private Executor delayedExecutor(long delayInSeconds, TimeUnit unit, Executor executor) {
        return runnable -> executorService.schedule(() -> executor.execute(runnable), delayInSeconds, unit);
    }

    @Override
    public List<String> getFiatCurrencies() {
        return ratesCache.getFiatCurrencies();
    }

    @Override
    public List<String> getCryptoCurrencies() {
        return ratesCache.getCryptoCurrencies();
    }
}
