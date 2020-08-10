package io.github.rates.transform.strategies;

import io.github.rates.domain.TransformStrategyType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TransformStrategy {

    Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo);

    CompletableFuture<BigDecimal> transformAsynchronously(BigDecimal amount, String currencyFrom, String currencyTo);

    TransformStrategyType getType();

}
