package io.github.rates.transform;

import io.github.rates.domain.TransformStrategyType;
import io.github.rates.transform.strategies.TransformStrategy;
import io.github.rates.transform.strategies.TransformStrategyFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TransformStrategiesExecutor {

    private final TransformStrategyTypeQualifier transformStrategyTypeQualifier;
    private final Map<TransformStrategyType, TransformStrategy> transformStrategies;

    public TransformStrategiesExecutor(TransformStrategyFactory transformStrategyFactory) {
        this(TransformStrategyTypeQualifier.getInstance(), transformStrategyFactory);
    }

    public TransformStrategiesExecutor(
            TransformStrategyTypeQualifier transformStrategyTypeQualifier,
            TransformStrategyFactory transformStrategyFactory
    ) {
        this.transformStrategyTypeQualifier = transformStrategyTypeQualifier;
        this.transformStrategies = transformStrategyFactory.getTransformStrategiesAsMap();
    }

    public CompletableFuture<BigDecimal> transformAsync(BigDecimal amount, String currencyFrom, String currencyTo) {
        return currencyFrom.equalsIgnoreCase(currencyTo)
                ? CompletableFuture.completedFuture(amount)
                : getTransformStrategy(currencyFrom, currencyTo).transformAsynchronously(amount, currencyFrom, currencyTo);
    }

    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return currencyFrom.equalsIgnoreCase(currencyTo)
                ? Optional.of(amount)
                : getTransformStrategy(currencyFrom, currencyTo).transform(amount, currencyFrom, currencyTo);
    }

    private TransformStrategy getTransformStrategy(String currencyFrom, String currencyTo) {
        return transformStrategies.get(transformStrategyTypeQualifier.getType(currencyFrom, currencyTo));
    }
}
