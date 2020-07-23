package io.github.rates.communicators.binance;

import io.github.rates.communicators.CommunicatorsAPIConfigurations;
import io.github.rates.tools.json.UncheckedObjectMapper;

public class BinanceCommunicatorBuilder {

    public static BinanceRatesSupplierCommunicator newBinanceRatesSupplierCommunicator() {
        return new BinanceRatesSupplierCommunicator(createBinanceAPI(), new BinanceResponsesToModelMapper());
    }

    private static BinanceApi createBinanceAPI() {
        return new BinanceApi(CommunicatorsAPIConfigurations.createBinanceHttpClient(), new UncheckedObjectMapper());
    }

}
