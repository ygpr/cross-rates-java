package io.github.rates.suppliers;

import io.github.rates.communicators.RatesProvidingCommunicator;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AbstractTargetRatesSupplier implements TargetRatesSupplier {

    private final RatesProvidingCommunicator ratesProvidingCommunicator;

    public AbstractTargetRatesSupplier(RatesProvidingCommunicator ratesProvidingCommunicator) {
        this.ratesProvidingCommunicator = ratesProvidingCommunicator;

    }

    @Override
    public CompletableFuture<List<Rate>> getRatesFromTarget() {
        return ratesProvidingCommunicator.getRates();
    }
}
