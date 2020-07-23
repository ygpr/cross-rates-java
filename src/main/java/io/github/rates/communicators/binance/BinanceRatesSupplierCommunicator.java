package io.github.rates.communicators.binance;

import io.github.rates.communicators.RatesSupplierCommunicator;
import io.github.rates.communicators.binance.model.response.ExchangeInfoResponse;
import io.github.rates.communicators.binance.model.response.RateResponse;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

class BinanceRatesSupplierCommunicator implements RatesSupplierCommunicator {

    private final BinanceApi binanceApi;
    private final BinanceResponsesToModelMapper modelMapper;

    BinanceRatesSupplierCommunicator(BinanceApi binanceApi, BinanceResponsesToModelMapper modelMapper) {
        this.binanceApi = binanceApi;
        this.modelMapper = modelMapper;
    }

    @Override
    public CompletableFuture<List<Rate>> getRates() {
        try {
            return binanceApi
                    .sendRequestForRates()
                    .thenCombine(binanceApi.sendRequestForExchangeInfo(), mapResponsesToRate());
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred on rates supply from Binance API", e);
        }
    }

    private BiFunction<List<RateResponse>, ExchangeInfoResponse, List<Rate>> mapResponsesToRate() {
        return (rateResponses, exchangeInfoResponse) -> modelMapper.mapToRate(rateResponses, exchangeInfoResponse.getSymbols());
    }

}
