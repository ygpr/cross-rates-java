package io.github.rates.suppliers.transformers.strategies;

import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.suppliers.transformers.TransformStrategyType;

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

    TransformOperations getTransformOperationsFactory() {
        return TransformOperations.getInstance(ratesSupplier);
    }

    FiatToFiatTransformStrategy getFiatToFiatTransformStrategy() {
        return FiatToFiatTransformStrategy.getInstance(getTransformOperationsFactory());
    }

    CryptoToCryptoTransformStrategy getCryptoToCryptoTransformStrategy() {
        return CryptoToCryptoTransformStrategy.getInstance(getTransformOperationsFactory());
    }

    CryptoToFiatTransformStrategy getCryptoToFiatTransformStrategy() {
        return CryptoToFiatTransformStrategy.getInstance(getTransformOperationsFactory(), getFiatToFiatTransformStrategy());
    }

    FiatToCryptoTransformStrategy getFiatToCryptoTransformStrategy() {
        return FiatToCryptoTransformStrategy.getInstance(
                getTransformOperationsFactory(),
                getCryptoToCryptoTransformStrategy(),
                getFiatToFiatTransformStrategy()
        );
    }

    Map<TransformStrategyType, TransformStrategy> getTransformStrategiesAsMap() {
        return Collections.unmodifiableMap(Map.of(
                TransformStrategyType.CRYPTO_TO_CRYPTO, getCryptoToCryptoTransformStrategy(),
                TransformStrategyType.CRYPTO_TO_FIAT, getCryptoToFiatTransformStrategy(),
                TransformStrategyType.FIAT_TO_CRYPTO, getFiatToCryptoTransformStrategy(),
                TransformStrategyType.FIAT_TO_FIAT, getFiatToFiatTransformStrategy()
        ));
    }

}
