package io.github.rates.suppliers;

import io.github.rates.communicators.monobank.MonobankCommunicatorBuilder;

public class MonobankTargetRatesProvider extends TargetRatesProvider {

    public MonobankTargetRatesProvider() {
        super(MonobankCommunicatorBuilder.newMonobankRatesProvidingCommunicator());
    }

}
