package io.github.rates.suppliers;

import io.github.rates.model.Rate;

import java.util.List;

public interface TargetRatesSupplier {

    List<Rate> getRatesFromTarget();

}
