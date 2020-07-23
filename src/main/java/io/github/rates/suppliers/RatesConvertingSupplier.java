package io.github.rates.suppliers;

import io.github.rates.cache.ExchangeRatesCache;
import io.github.rates.domain.Rate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Function;

public class RatesConvertingSupplier implements RateSupplier, RateConverter {

    private final ExchangeRatesCache exchangeRatesCache;
    private final ScheduledExecutorService executorService;

    public RatesConvertingSupplier(ExchangeRatesCache exchangeRatesCache, ScheduledExecutorService executorService) {
        this.exchangeRatesCache = exchangeRatesCache;
        this.executorService = executorService;
    }

    @Override
    public Optional<BigDecimal> convert(BigDecimal amount, String asset, String quotable) {
        return getRate(asset, quotable).map(convertToReceivedRate(amount));
    }

    @Override
    public CompletableFuture<BigDecimal> convertAsynchronously(BigDecimal amount, String asset, String quotable) {
        return getRateAsynchronously(asset, quotable).thenApply(convertToReceivedRate(amount));
    }

    @Override
    public CompletableFuture<BigDecimal> convertAsynchronously(BigDecimal amount, String asset, String quotable, long delayInSeconds) {
        return getRateAsynchronously(asset, quotable, delayInSeconds).thenApply(convertToReceivedRate(amount));
    }

    @Override
    public Optional<Rate> getRate(String asset, String quotable) {
        return Optional.ofNullable(exchangeRatesCache.getCryptoCurrencyRate(asset + quotable));
    }

    @Override
    public CompletableFuture<Rate> getRateAsynchronously(String asset, String quotable) {
        return CompletableFuture.supplyAsync(() -> exchangeRatesCache.getCryptoCurrencyRate(asset + quotable));
    }

    @Override
    public CompletableFuture<Rate> getRateAsynchronously(String asset, String quotable, long delayInSeconds) {
        return CompletableFuture.supplyAsync(
                () -> exchangeRatesCache.getCryptoCurrencyRate(asset + quotable),
                delayedExecutor(delayInSeconds, TimeUnit.SECONDS)
        );
    }

    private Function<Rate, BigDecimal> convertToReceivedRate(BigDecimal amount) {
        return rate -> rate == null ? null : rate.getPrice().multiply(amount);
    }

    private Executor delayedExecutor(long delayInSeconds, TimeUnit unit) {
        return delayedExecutor(delayInSeconds, unit, ForkJoinPool.commonPool());
    }

    private Executor delayedExecutor(long delayInSeconds, TimeUnit unit, Executor executor) {
        return runnable -> executorService.schedule(() -> executor.execute(runnable), delayInSeconds, unit);
    }
}
