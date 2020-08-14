package io.github.rates.transform;

import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.transform.TransformStrategiesExecutor;
import io.github.rates.transform.strategies.TransformStrategyFactory;

public class TransformExecutorsFactory {

    public static TransformStrategiesExecutor buildWith(RatesSupplier ratesSupplier) {
        return new TransformStrategiesExecutor(TransformStrategyFactory.getInstance(ratesSupplier));
    }

}
