package io.github.rates.cache;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurrencyRatesCache {

    private final NavigableMap<String, Rate> pairToRate = new ConcurrentSkipListMap<>(CASE_INSENSITIVE_ORDER);
    private final NavigableMap<String, CompletableFuture<Rate>> pairToFuture = new ConcurrentSkipListMap<>(CASE_INSENSITIVE_ORDER);
    private volatile boolean loaded;

    public Rate getCryptoCurrencyRate(String pairName) {
        return pairToRate.get(pairName);
    }

    public synchronized CompletableFuture<Rate> getCryptoCurrencyRateAsync(String pairName) {
        if (loaded) {
            return CompletableFuture.completedFuture(pairToRate.get(pairName));
        }
        return pairToFuture.computeIfAbsent(pairName, p -> new CompletableFuture<>());
    }

    public synchronized CompletableFuture<Optional<Rate>> findCryptoCurrencyRateAsync(String pairName) {
        return getCryptoCurrencyRateAsync(pairName).thenApply(Optional::ofNullable);
    }

    public synchronized void updateCurrenciesRates(List<Rate> cryptoCurrenciesRatesToAdd) {
        cryptoCurrenciesRatesToAdd.forEach(this::updateRate);
    }

    private void updateRate(Rate toAdd) {
        loaded = true;
        pairToRate.put(toAdd.getPairName(), toAdd);
        Optional.ofNullable(pairToFuture.remove(toAdd.getPairName()))
                .ifPresent(future -> future.completeAsync(() -> toAdd));
    }

    public void clear() {
        pairToRate.clear();
    }

    public List<String> getFiatCurrencies() {
        return pairToRate.values().stream()
                .filter(Predicate.not(Rate::isCrypto))
                .flatMap(rate -> Stream.of(rate.getAsset(), rate.getQuotable()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getCryptoCurrencies() {
        List<String> fiatCurrencies = getFiatCurrencies();
        return pairToRate.values().stream()
                .filter(Rate::isCrypto)
                .flatMap(rate -> Stream.of(rate.getAsset(), rate.getQuotable()))
                .distinct()
                .filter(Predicate.not(fiatCurrencies::contains))
                .sorted()
                .collect(Collectors.toList());
    }
}
