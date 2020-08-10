package io.github.rates.suppliers;

import io.github.rates.communicators.binance.BinanceCommunicatorBuilder;

public class BinanceTargetRatesProvider extends TargetRatesProvider {

    public BinanceTargetRatesProvider() {
        super(BinanceCommunicatorBuilder.newBinanceRatesProvidingCommunicator());
    }

}
