package io.github.rates.suppliers;

import io.github.rates.communicators.RatesSupplierCommunicator;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbstractTargetRatesSupplier implements TargetRatesSupplier {

    private final RatesSupplierCommunicator ratesSupplierCommunicator;

    public AbstractTargetRatesSupplier(RatesSupplierCommunicator ratesSupplierCommunicator) {
        this.ratesSupplierCommunicator = ratesSupplierCommunicator;

    }

    @Override
    public CompletableFuture<List<Rate>> getRatesFromTarget() {
        return ratesSupplierCommunicator.getRates();
    }
}
