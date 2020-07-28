package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.CRYPTO_TO_CRYPTO;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;

import io.github.rates.domain.TransformStrategyType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class CryptoToCryptoTransformStrategy implements TransformStrategy {

    private static volatile CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategyInstance;

    private final TransformOperations transformOperations;

    private CryptoToCryptoTransformStrategy(TransformOperations transformOperations) {
        this.transformOperations = transformOperations;
    }

    static CryptoToCryptoTransformStrategy getInstance(TransformOperations transformOperations) {
        if (cryptoToCryptoTransformStrategyInstance == null) {
            synchronized (CryptoToCryptoTransformStrategy.class) {
                if (cryptoToCryptoTransformStrategyInstance == null) {
                    cryptoToCryptoTransformStrategyInstance = new CryptoToCryptoTransformStrategy(transformOperations);
                }
            }
        }
        return cryptoToCryptoTransformStrategyInstance;
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo)
                .or(() -> convertViaBitcoin(amount, currencyFrom, currencyTo));
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
        return CRYPTO_TO_CRYPTO;
    }


    private Optional<BigDecimal> convertViaBitcoin(BigDecimal amount, String currencyFrom, String currencyTo) {
        return transformOperations
                .transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER)
                .flatMap(toBtcPrice -> transformOperations.transformCryptoCurrencies(toBtcPrice, BITCOIN_TICKER, currencyTo));
    }

}
