package io.github.rates.suppliers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RateConverter {

    Optional<BigDecimal> convert(BigDecimal amount, String asset, String quotable);

    CompletableFuture<BigDecimal> convertAsynchronously(BigDecimal amount, String asset, String quotable);

    CompletableFuture<BigDecimal> convertAsynchronously(BigDecimal amount, String asset, String quotable, long delayInSeconds);


}
