package io.github.rates.communicators.binance;

import io.github.rates.communicators.CommunicatorsAPIConfigurations;
import io.github.rates.communicators.RatesJsonSupplier;

public class BinanceRatesJsonSupplier implements RatesJsonSupplier {

    private final BinanceApi binanceApi;

    public BinanceRatesJsonSupplier() {
        this(new BinanceApi(CommunicatorsAPIConfigurations.createBinanceHttpClient()));
    }

    public BinanceRatesJsonSupplier(BinanceApi binanceApi) {
        this.binanceApi = binanceApi;
    }

    @Override
    public String getRatesAsJson() {
        try {
            return binanceApi.sendRequestForRates();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred on rates request to Binance API", e);
        }
    }
}
