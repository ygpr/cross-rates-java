package io.github.rates;

import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.transform.TransformExecutorsFactory;
import io.github.rates.transform.TransformStrategiesExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DefaultCrossRatesAPI implements CrossRatesAPI {

    private final RatesSupplier ratesSupplier;
    private final TransformStrategiesExecutor strategiesExecutor;

    public DefaultCrossRatesAPI(RatesSupplier ratesSupplier) {
        this.ratesSupplier = ratesSupplier;
        this.strategiesExecutor = TransformExecutorsFactory.buildWith(ratesSupplier);
    }

    @Override
    public CompletableFuture<BigDecimal> transformAsync(BigDecimal amount, String currencyFrom, String currencyTo) {
        return strategiesExecutor.transformAsync(amount, currencyFrom, currencyTo);
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return strategiesExecutor.transform(amount, currencyFrom, currencyTo);
    }

    @Override
    public Optional<Rate> getRate(String asset, String quotable) {
        return strategiesExecutor.transform(BigDecimal.ONE, asset, quotable)
                .map(price -> new Rate(
                        asset, quotable, asset + quotable, 8, 8, price
                ));
    }

    @Override
    public CompletableFuture<Optional<Rate>> getRateAsync(String asset, String quotable) {
        return strategiesExecutor.transformAsync(BigDecimal.ONE, asset, quotable)
                .thenApply(price -> Optional.of(new Rate(
                        asset, quotable, asset + quotable, 8, 8, price
                )));
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
