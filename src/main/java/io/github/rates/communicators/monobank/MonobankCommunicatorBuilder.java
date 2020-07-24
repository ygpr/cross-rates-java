package io.github.rates.communicators.monobank;

import io.github.rates.communicators.CommunicatorsAPIConfigurations;
import io.github.rates.tools.currency.ISO4217CodeToCurrencyTicker;
import io.github.rates.tools.json.UncheckedObjectMapper;

public class MonobankCommunicatorBuilder {

    public static MonobankRatesProvidingCommunicator newMonobankRatesProvidingCommunicator() {
        return new MonobankRatesProvidingCommunicator(createMonobankAPI(), createModelMapper());
    }

    private static MonobankResponseToModelMapper createModelMapper() {
        return new MonobankResponseToModelMapper(
                new ISO4217CodeToCurrencyTicker(),
                new MonobankRatePriceFromResponseCalculator()
        );
    }

    private static MonobankRestApi createMonobankAPI() {
        return new MonobankRestApi(
                CommunicatorsAPIConfigurations.createMonobankHttpClient(),
                new UncheckedObjectMapper()
        );
    }

}
