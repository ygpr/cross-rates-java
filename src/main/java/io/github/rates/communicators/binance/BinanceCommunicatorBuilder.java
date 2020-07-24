package io.github.rates.communicators.binance;

import io.github.rates.communicators.CommunicatorsAPIConfigurations;
import io.github.rates.tools.json.UncheckedObjectMapper;

public class BinanceCommunicatorBuilder {

    public static BinanceRatesProvidingCommunicator newBinanceRatesProvidingCommunicator() {
        return new BinanceRatesProvidingCommunicator(
                createBinanceAPI(),
                new BinanceResponsesToModelMapper()
        );
    }

    private static BinanceRestApi createBinanceAPI() {
        return new BinanceRestApi(
                CommunicatorsAPIConfigurations.createBinanceHttpClient(),
                new UncheckedObjectMapper()
        );
    }

}
