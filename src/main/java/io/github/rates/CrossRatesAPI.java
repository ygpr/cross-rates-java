package io.github.rates;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrossRatesAPI {

    Optional<Rate> getRate(String asset, String quotable);

    CompletableFuture<Optional<Rate>> getRateAsync(String asset, String quotable);

    List<String> getCurrencies();

    List<String> getFiatCurrencies();

    List<String> getCryptoCurrencies();
}
