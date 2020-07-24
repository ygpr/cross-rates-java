package io.github.rates.communicators.monobank;

import io.github.rates.communicators.RatesProvidingCommunicator;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class MonobankRatesProvidingCommunicator implements RatesProvidingCommunicator {

    private final MonobankRestApi monobankApi;
    private final MonobankResponseToModelMapper modelMapper;

    MonobankRatesProvidingCommunicator(MonobankRestApi monobankApi, MonobankResponseToModelMapper modelMapper) {
        this.monobankApi = monobankApi;
        this.modelMapper = modelMapper;
    }

    @Override
    public CompletableFuture<List<Rate>> getRates() {
        try {
            return monobankApi
                    .sendRequestForRates()
                    .thenApply(modelMapper::mapToRate);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred on rates supply from Monobank API", e);
        }
    }

}
