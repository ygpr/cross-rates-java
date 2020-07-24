package io.github.rates.communicators;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RatesProvidingCommunicator {

    CompletableFuture<List<Rate>> getRates();

}
