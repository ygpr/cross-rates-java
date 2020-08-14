package io.github.rates;

import io.github.rates.domain.Rate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrossRatesAPI {

    List<String> getCurrencies();

    List<String> getFiatCurrencies();

    List<String> getCryptoCurrencies();

    Optional<Rate> getRate(String asset, String quotable);

    CompletableFuture<Optional<Rate>> getRateAsync(String asset, String quotable);

    Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo);

    CompletableFuture<BigDecimal> transformAsync(BigDecimal amount, String currencyFrom, String currencyTo);

}
