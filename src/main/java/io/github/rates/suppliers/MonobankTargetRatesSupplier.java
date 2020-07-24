package io.github.rates.suppliers;

import io.github.rates.communicators.monobank.MonobankCommunicatorBuilder;

public class MonobankTargetRatesSupplier extends TargetRatesSupplier {

    public MonobankTargetRatesSupplier() {
        super(MonobankCommunicatorBuilder.newMonobankRatesProvidingCommunicator());
    }

}
