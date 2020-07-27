package io.github.rates.cache;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;

public class CurrencyRatesCache {

    private final NavigableMap<String, Rate> cryptoCurrenciesRates = new ConcurrentSkipListMap<>(CASE_INSENSITIVE_ORDER);
    private final NavigableMap<String, CompletableFuture<Rate>> pairToFuture = new ConcurrentSkipListMap<>(CASE_INSENSITIVE_ORDER);

    public Rate getCryptoCurrencyRate(String pairName) {
        return cryptoCurrenciesRates.get(pairName);
    }

    public synchronized CompletableFuture<Rate> getCryptoCurrencyRateAsync(String pairName) {
        Rate rate = cryptoCurrenciesRates.get(pairName);
        if (rate != null) {
            return CompletableFuture.completedFuture(rate);
        }
        return pairToFuture.computeIfAbsent(pairName, p -> new CompletableFuture<>());
    }

    public synchronized void updateCryptoCurrenciesRates(List<Rate> cryptoCurrenciesRatesToAdd) {
        cryptoCurrenciesRatesToAdd.forEach(this::updateCryptoCurrencyRate);
    }

    private void updateCryptoCurrencyRate(Rate toAdd) {
        cryptoCurrenciesRates.put(toAdd.getPairName(), toAdd);
        Optional.ofNullable(pairToFuture.remove(toAdd.getPairName()))
                .ifPresent(future -> future.completeAsync(() -> toAdd));
    }

    public void clear() {
        cryptoCurrenciesRates.clear();
    }

}
