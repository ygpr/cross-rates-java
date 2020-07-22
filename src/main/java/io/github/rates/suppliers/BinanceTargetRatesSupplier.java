package io.github.rates.suppliers;

import io.github.rates.communicators.binance.BinanceRatesJsonSupplier;
import io.github.rates.parsers.ParsersFactory;

public class BinanceTargetRatesSupplier extends AbstractTargetRatesSupplier {

    public BinanceTargetRatesSupplier() {
        super(new BinanceRatesJsonSupplier(), ParsersFactory.createRatesJsonParser());
    }

}
