package io.github.rates.cache;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.domain.Rate;
import io.github.rates.suppliers.TargetRatesSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class CurrencyRatesCacheUpdaterTest {

    @Mock
    private TargetRatesSupplier targetRatesSupplier;

    @Mock
    private CacheUpdateProgram cacheUpdateProgram;

    @Test
    void startProgram() {
        CurrencyRatesCache cache = new CurrencyRatesCache();
        CurrencyRatesCacheUpdater cryptoCurrencyRatesCacheUpdater = new CurrencyRatesCacheUpdater(
                cache, targetRatesSupplier, cacheUpdateProgram
        );
        Rate rate = new Rate(
                "TEST", "NAME", "TESTNAME", 8, 8, BigDecimal.TEN
        );
        given(cacheUpdateProgram.getExecutorService()).willReturn(Executors.newSingleThreadScheduledExecutor());
        given(cacheUpdateProgram.getDelay()).willReturn(Long.valueOf(1));
        given(cacheUpdateProgram.getInitialDelay()).willReturn(Long.valueOf(1));
        given(cacheUpdateProgram.getTimeUnit()).willReturn(TimeUnit.SECONDS);
        given(targetRatesSupplier.getRatesFromTarget()).willReturn(CompletableFuture.completedFuture(List.of(rate)));

        cryptoCurrencyRatesCacheUpdater.startProgram();
        await().until(() -> cache.getCryptoCurrencyRate(rate.getPairName()) != null);

        assertEquals(rate, cache.getCryptoCurrencyRate(rate.getPairName()));
    }
}
