package io.github.rates.suppliers;

import io.github.rates.communicators.binance.BinanceCommunicatorBuilder;

public class BinanceTargetRatesSupplier extends AbstractTargetRatesSupplier {

    public BinanceTargetRatesSupplier() {
        super(BinanceCommunicatorBuilder.newBinanceRatesProvidingCommunicator());
    }

}
