package io.github.rates.suppliers;

import io.github.rates.communicators.RatesJsonSupplier;
import io.github.rates.model.Rate;
import io.github.rates.parsers.RatesJsonParser;

import java.util.List;

public class AbstractTargetRatesSupplier implements TargetRatesSupplier {

    private final RatesJsonSupplier ratesJsonSupplier;
    private final RatesJsonParser ratesJsonParser;

    public AbstractTargetRatesSupplier(RatesJsonSupplier ratesJsonSupplier, RatesJsonParser ratesJsonParser) {
        this.ratesJsonSupplier = ratesJsonSupplier;
        this.ratesJsonParser = ratesJsonParser;
    }

    @Override
    public List<Rate> getRatesFromTarget() {
        return ratesJsonParser.parseJson(ratesJsonSupplier.getRatesAsJson());
    }
}
