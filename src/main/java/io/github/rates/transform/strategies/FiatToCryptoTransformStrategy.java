package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.FIAT_TO_CRYPTO;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;
import static io.github.rates.transform.strategies.TransformOperations.EURO_TICKER;

import io.github.rates.domain.TransformStrategyType;
import io.github.rates.tools.math.CurrencyConvertingDecimal;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class FiatToCryptoTransformStrategy implements TransformStrategy {

    private static volatile FiatToCryptoTransformStrategy fiatToCryptoTransformStrategyInstance;

    private final TransformOperations transformOperations;
    private final FiatToFiatTransformStrategy fiatToFiatTransformStrategy;
    private final CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategy;

    private FiatToCryptoTransformStrategy(
            TransformOperations transformOperations,
            CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategy,
            FiatToFiatTransformStrategy fiatToFiatTransformStrategy
    ) {
        this.transformOperations = transformOperations;
        this.cryptoToCryptoTransformStrategy = cryptoToCryptoTransformStrategy;
        this.fiatToFiatTransformStrategy = fiatToFiatTransformStrategy;
    }

    static FiatToCryptoTransformStrategy getInstance(
            TransformOperations transformOperations,
            CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategy,
            FiatToFiatTransformStrategy fiatToFiatTransformStrategy
    ) {
        if (fiatToCryptoTransformStrategyInstance == null) {
            synchronized (FiatToCryptoTransformStrategy.class) {
                if (fiatToCryptoTransformStrategyInstance == null) {
                    fiatToCryptoTransformStrategyInstance = new FiatToCryptoTransformStrategy(
                            transformOperations, cryptoToCryptoTransformStrategy, fiatToFiatTransformStrategy
                    );
                }
            }
        }
        return fiatToCryptoTransformStrategyInstance;
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return getAmountInEur(amount, currencyFrom)
                .flatMap(mapEurAmountToBtc())
                .flatMap(getBtcAmountTransformedToRequestedCurrency(currencyTo));
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
        return FIAT_TO_CRYPTO;
    }

    private Function<BigDecimal, Optional<BigDecimal>> mapEurAmountToBtc() {
        return eurAmount -> transformOperations
                .getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER)
                .map(btcToEurPrice -> CurrencyConvertingDecimal.from(eurAmount).divideWithDefaultScaling(btcToEurPrice));
    }

    private Optional<BigDecimal> getAmountInEur(BigDecimal amount, String currencyFrom) {
        return currencyFrom.equalsIgnoreCase(EURO_TICKER)
                ? Optional.ofNullable(amount)
                : fiatToFiatTransformStrategy.transform(amount, currencyFrom, EURO_TICKER);
    }

    private Function<BigDecimal, Optional<BigDecimal>> getBtcAmountTransformedToRequestedCurrency(String currencyTo) {
        return btcAmount -> currencyTo.equalsIgnoreCase(BITCOIN_TICKER)
                ? Optional.of(btcAmount)
                : cryptoToCryptoTransformStrategy.transform(btcAmount, BITCOIN_TICKER, currencyTo);
    }

}
