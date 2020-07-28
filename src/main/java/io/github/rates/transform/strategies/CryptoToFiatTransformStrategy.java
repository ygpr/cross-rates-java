package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.CRYPTO_TO_FIAT;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;
import static io.github.rates.transform.strategies.TransformOperations.EURO_TICKER;

import io.github.rates.domain.TransformStrategyType;
import io.github.rates.tools.math.CurrencyConvertingDecimal;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

class CryptoToFiatTransformStrategy implements TransformStrategy {

    private static volatile CryptoToFiatTransformStrategy cryptoToFiatTransformStrategyInstance;

    private final TransformOperations transformOperations;
    private final FiatToFiatTransformStrategy fiatToFiatTransformStrategy;

    private CryptoToFiatTransformStrategy(
            TransformOperations transformOperations,
            FiatToFiatTransformStrategy fiatToFiatTransformStrategy
    ) {
        this.transformOperations = transformOperations;
        this.fiatToFiatTransformStrategy = fiatToFiatTransformStrategy;
    }

    static CryptoToFiatTransformStrategy getInstance(
            TransformOperations transformOperations,
            FiatToFiatTransformStrategy fiatToFiatTransformStrategy
    ) {
        if (cryptoToFiatTransformStrategyInstance == null) {
            synchronized (CryptoToFiatTransformStrategy.class) {
                if (cryptoToFiatTransformStrategyInstance == null) {
                    cryptoToFiatTransformStrategyInstance = new CryptoToFiatTransformStrategy(
                            transformOperations, fiatToFiatTransformStrategy
                    );
                }
            }
        }
        return cryptoToFiatTransformStrategyInstance;
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return getAmountInBtc(amount, currencyFrom)
                .flatMap(mapBtcAmountToEuro())
                .flatMap(getAmountTransformedToRequestedCurrency(currencyFrom, currencyTo));
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
        return CRYPTO_TO_FIAT;
    }

    private Function<BigDecimal, Optional<BigDecimal>> mapBtcAmountToEuro() {
        return btcAmount -> transformOperations
                .getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER)
                .map(CurrencyConvertingDecimal::new )
                .map(btcToEur -> btcToEur.multiply(btcAmount));
    }

    private Optional<BigDecimal> getAmountInBtc(BigDecimal amount, String currencyFrom) {
        return currencyFrom.equalsIgnoreCase(BITCOIN_TICKER)
                ? Optional.of(amount)
                : transformOperations.transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER);
    }

    private Function<BigDecimal, Optional<BigDecimal>> getAmountTransformedToRequestedCurrency(String currencyFrom, String currencyTo) {
        return amountInEur -> isBitcoinTransformingToEuro(currencyFrom, currencyTo)
                ? Optional.of(amountInEur)
                : fiatToFiatTransformStrategy.transform(amountInEur, EURO_TICKER, currencyTo);
    }

    private boolean isBitcoinTransformingToEuro(String currencyFrom, String currencyTo) {
        return currencyFrom.equalsIgnoreCase(BITCOIN_TICKER) && currencyTo.equalsIgnoreCase(EURO_TICKER);
    }

}
