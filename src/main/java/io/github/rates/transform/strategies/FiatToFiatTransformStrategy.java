package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.FIAT_TO_FIAT;
import static io.github.rates.transform.strategies.TransformOperations.UKRAINIAN_HRYVNIA_TICKER;

import io.github.rates.domain.Rate;
import io.github.rates.domain.TransformStrategyType;
import io.github.rates.tools.math.CurrencyConvertingDecimal;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class FiatToFiatTransformStrategy implements TransformStrategy {

    private static volatile FiatToFiatTransformStrategy fiatToFiatTransformStrategyInstance;

    private final PrecisionNormalizer precisionNormalizer;
    private final TransformOperations transformOperations;

    private FiatToFiatTransformStrategy(
            TransformOperations transformOperations,
            PrecisionNormalizer precisionNormalizer
    ) {
        this.transformOperations = transformOperations;
        this.precisionNormalizer = precisionNormalizer;
    }

    static FiatToFiatTransformStrategy getInstance(
            TransformOperations transformOperations,
            PrecisionNormalizer precisionNormalizer
    ) {
        if (fiatToFiatTransformStrategyInstance == null) {
            synchronized (FiatToFiatTransformStrategy.class) {
                if (fiatToFiatTransformStrategyInstance == null) {
                    fiatToFiatTransformStrategyInstance = new FiatToFiatTransformStrategy(
                            transformOperations, precisionNormalizer
                    );
                }
            }
        }
        return fiatToFiatTransformStrategyInstance;
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        if (currencyFrom.equalsIgnoreCase(currencyTo)) {
            return Optional.of(amount);
        }
        return getAmountInUah(amount, currencyFrom)
                .flatMap(getAmountInHryvniaTransformedTo(currencyTo))
                .map(result -> precisionNormalizer.normalize(result, currencyTo));
    }

    @Override
    public CompletableFuture<BigDecimal> transformAsynchronously(BigDecimal amount, String currencyFrom, String currencyTo) {
        return CompletableFuture.supplyAsync(() -> transform(amount, currencyFrom, currencyTo)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Can't transform %s to %s, one of currencies not found", currencyFrom, currencyTo)
                )));
    }

    @Override
    public TransformStrategyType getType() {
        return FIAT_TO_FIAT;
    }

    private Optional<BigDecimal> getAmountInUah(BigDecimal amount, String currencyFrom) {
        return currencyFrom.equalsIgnoreCase(UKRAINIAN_HRYVNIA_TICKER)
                ? Optional.of(amount)
                : transformOperations.getRate(currencyFrom, UKRAINIAN_HRYVNIA_TICKER)
                .map(Rate::getPrice)
                .map(CurrencyConvertingDecimal::new)
                .map(uahRate -> uahRate.multiply(amount));
    }

    private Function<BigDecimal, Optional<BigDecimal>> getAmountInHryvniaTransformedTo(String currencyTo) {
        return amountInUah -> isTransformingToHryvnia(currencyTo)
                ? Optional.of(amountInUah)
                : transformViaCrossCourse(currencyTo, amountInUah);
    }

    private Optional<BigDecimal> transformViaCrossCourse(String currencyTo, BigDecimal amountInUah) {
        return transformOperations.getRate(currencyTo, UKRAINIAN_HRYVNIA_TICKER)
                .map(Rate::getPrice)
                .map(crossRate -> CurrencyConvertingDecimal.from(amountInUah).divideWithDefaultScaling(crossRate));
    }

    private boolean isTransformingToHryvnia(String currencyTo) {
        return currencyTo.equalsIgnoreCase(UKRAINIAN_HRYVNIA_TICKER);
    }

}
