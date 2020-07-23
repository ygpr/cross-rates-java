package io.github.rates.suppliers;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TargetRatesSupplier {

    CompletableFuture<List<Rate>> getRatesFromTarget();

}
