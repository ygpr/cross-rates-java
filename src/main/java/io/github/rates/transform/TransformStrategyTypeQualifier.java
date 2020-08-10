package io.github.rates.transform;

import static io.github.rates.domain.TransformStrategyType.*;

import io.github.rates.domain.CurrencyComprassionResult;
import io.github.rates.domain.TransformStrategyType;
import io.github.rates.tools.currency.ISO4217CodeToCurrency;

import java.util.Collections;
import java.util.Map;

class TransformStrategyTypeQualifier {

    private static volatile TransformStrategyTypeQualifier transformStrategyTypeQualifierInstance;

    private final ISO4217CodeToCurrency iso4217CodeToCurrency;
    private final Map<CurrencyComprassionResult, TransformStrategyType> resultToTypeMap;

    TransformStrategyTypeQualifier(
            ISO4217CodeToCurrency iso4217CodeToCurrency,
            Map<CurrencyComprassionResult, TransformStrategyType> resultToTypeMap
    ) {
        this.iso4217CodeToCurrency = iso4217CodeToCurrency;
        this.resultToTypeMap = resultToTypeMap;
    }

    static TransformStrategyTypeQualifier getInstance() {
        if (transformStrategyTypeQualifierInstance == null) {
            synchronized (TransformStrategyTypeQualifier.class) {
                if (transformStrategyTypeQualifierInstance == null) {
                    transformStrategyTypeQualifierInstance = new TransformStrategyTypeQualifier(
                            ISO4217CodeToCurrency.getInstance(),
                            getResultToTypeMap());
                }
            }
        }
        return transformStrategyTypeQualifierInstance;
    }

    static Map<CurrencyComprassionResult, TransformStrategyType> getResultToTypeMap() {
        return Collections.unmodifiableMap(Map.of(
                new CurrencyComprassionResult(true, true), FIAT_TO_FIAT,
                new CurrencyComprassionResult(true, false), FIAT_TO_CRYPTO,
                new CurrencyComprassionResult(false, true), CRYPTO_TO_FIAT,
                new CurrencyComprassionResult(false, false), CRYPTO_TO_CRYPTO
        ));
    }

    TransformStrategyType getType(String from, String to) {
        return resultToTypeMap.get(
                new CurrencyComprassionResult(iso4217CodeToCurrency.isFiat(from), iso4217CodeToCurrency.isFiat(to))
        );
    }


}
