package io.github.rates.transform.strategies;

import io.github.rates.domain.TransformStrategyType;
import io.github.rates.suppliers.RatesSupplier;

import java.util.Collections;
import java.util.Map;

public class TransformStrategyFactory {

    private static TransformStrategyFactory transformStrategyFactoryInstance;

    private final RatesSupplier ratesSupplier;

    private TransformStrategyFactory(RatesSupplier ratesSupplier) {
        this.ratesSupplier = ratesSupplier;
    }

    public static TransformStrategyFactory getInstance(RatesSupplier ratesSupplier) {
        if (transformStrategyFactoryInstance == null) {
            synchronized (TransformStrategyFactory.class) {
                if (transformStrategyFactoryInstance == null) {
                    transformStrategyFactoryInstance = new TransformStrategyFactory(ratesSupplier);
                }
            }
        }
        return transformStrategyFactoryInstance;
    }

    public Map<TransformStrategyType, TransformStrategy> getTransformStrategiesAsMap() {
        return Collections.unmodifiableMap(Map.of(
                TransformStrategyType.CRYPTO_TO_CRYPTO, getCryptoToCryptoTransformStrategy(),
                TransformStrategyType.CRYPTO_TO_FIAT, getCryptoToFiatTransformStrategy(),
                TransformStrategyType.FIAT_TO_CRYPTO, getFiatToCryptoTransformStrategy(),
                TransformStrategyType.FIAT_TO_FIAT, getFiatToFiatTransformStrategy()
        ));
    }

    FiatToFiatTransformStrategy getFiatToFiatTransformStrategy() {
        return FiatToFiatTransformStrategy.getInstance(getTransformOperationsFactory(), getPrecisionNormalizer());
    }

    CryptoToCryptoTransformStrategy getCryptoToCryptoTransformStrategy() {
        return CryptoToCryptoTransformStrategy.getInstance(getTransformOperationsFactory(), getPrecisionNormalizer());
    }

    CryptoToFiatTransformStrategy getCryptoToFiatTransformStrategy() {
        return CryptoToFiatTransformStrategy.getInstance(
                getTransformOperationsFactory(),
                getPrecisionNormalizer(),
                getFiatToFiatTransformStrategy()
        );
    }

    FiatToCryptoTransformStrategy getFiatToCryptoTransformStrategy() {
        return FiatToCryptoTransformStrategy.getInstance(
                getTransformOperationsFactory(),
                getPrecisionNormalizer(),
                getCryptoToCryptoTransformStrategy(),
                getFiatToFiatTransformStrategy()
        );
    }

    private TransformOperations getTransformOperationsFactory() {
        return TransformOperations.getInstance(ratesSupplier);
    }

    private PrecisionNormalizer getPrecisionNormalizer() {
        return PrecisionNormalizer.getInstance(ratesSupplier);
    }

}
