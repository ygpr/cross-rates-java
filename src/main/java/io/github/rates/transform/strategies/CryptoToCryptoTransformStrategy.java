package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.CRYPTO_TO_CRYPTO;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;
import static io.github.rates.transform.strategies.TransformOperations.USD_TETHER_TICKER;

import io.github.rates.domain.TransformStrategyType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class CryptoToCryptoTransformStrategy implements TransformStrategy {

    private static volatile CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategyInstance;

    private final TransformOperations transformOperations;
    private final PrecisionNormalizer precisionNormalizer;

    private CryptoToCryptoTransformStrategy(
            TransformOperations transformOperations,
            PrecisionNormalizer precisionNormalizer
    ) {
        this.transformOperations = transformOperations;
        this.precisionNormalizer = precisionNormalizer;
    }

    static CryptoToCryptoTransformStrategy getInstance(
            TransformOperations transformOperations,
            PrecisionNormalizer precisionNormalizer
    ) {
        if (cryptoToCryptoTransformStrategyInstance == null) {
            synchronized (CryptoToCryptoTransformStrategy.class) {
                if (cryptoToCryptoTransformStrategyInstance == null) {
                    cryptoToCryptoTransformStrategyInstance = new CryptoToCryptoTransformStrategy(
                            transformOperations, precisionNormalizer
                    );
                }
            }
        }
        return cryptoToCryptoTransformStrategyInstance;
    }

    @Override
    public Optional<BigDecimal> transform(BigDecimal amount, String currencyFrom, String currencyTo) {
        return transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo)
                .or(() -> convertViaUsdtcoin(amount, currencyFrom, currencyTo))
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
        return CRYPTO_TO_CRYPTO;
    }


    private Optional<BigDecimal> convertViaUsdtcoin(BigDecimal amount, String currencyFrom, String currencyTo) {
        return transformOperations
                .transformCryptoCurrencies(amount, currencyFrom, USD_TETHER_TICKER)
                .flatMap(toUsdtPrice -> transformOperations.transformCryptoCurrencies(toUsdtPrice, USD_TETHER_TICKER, currencyTo));
    }

}
