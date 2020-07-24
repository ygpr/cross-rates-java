package io.github.rates.suppliers;

import io.github.rates.communicators.monobank.MonobankCommunicatorBuilder;

public class MonobankTargetRatesSupplier extends AbstractTargetRatesSupplier {

    public MonobankTargetRatesSupplier() {
        super(MonobankCommunicatorBuilder.newMonobankRatesProvidingCommunicator());
    }

}
