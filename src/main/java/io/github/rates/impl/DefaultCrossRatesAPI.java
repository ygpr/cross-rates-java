package io.github.rates.impl;

import io.github.rates.CrossRatesAPI;
import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DefaultCrossRatesAPI implements CrossRatesAPI {

    private final RatesSupplier ratesSupplier;

    public DefaultCrossRatesAPI(RatesSupplier ratesSupplier) {
        this.ratesSupplier = ratesSupplier;
    }


    @Override
    public Optional<Rate> getRate(String asset, String quotable) {
        return ratesSupplier.getRate(asset, quotable);
    }

    @Override
    public CompletableFuture<Optional<Rate>> getRateAsync(String asset, String quotable) {
        return ratesSupplier.getRateAsync(asset, quotable);
    }

    @Override
    public List<String> getCurrencies() {
        List<String> currencies = new ArrayList<>(getFiatCurrencies());
        currencies.addAll(getCryptoCurrencies());
        return currencies.stream().distinct().sorted().collect(Collectors.toList());
    }

    @Override
    public List<String> getFiatCurrencies() {
        return ratesSupplier.getFiatCurrencies();
    }

    @Override
    public List<String> getCryptoCurrencies() {
        return ratesSupplier.getCryptoCurrencies();
    }
}
