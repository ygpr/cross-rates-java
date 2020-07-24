package io.github.rates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.cache.CryptoCurrencyRatesCacheUpdater;
import io.github.rates.cache.CurrencyRatesCache;
import io.github.rates.configurations.CacheUpdateProgram;
import io.github.rates.domain.Rate;
import io.github.rates.suppliers.BinanceTargetRatesSupplier;
import io.github.rates.suppliers.RatesConvertingSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class IntegrationTest {

    @Mock
    private BinanceTargetRatesSupplier binanceTargetRatesSupplier;

    @Test
    void allFlowTest() throws Exception {
        String asset = "ADA";
        String quotable = "BTC";
        BigDecimal amount = BigDecimal.valueOf(3.04);
        Rate rate = new Rate(asset, quotable, asset + quotable, 8, 8, BigDecimal.valueOf(4550.9311));
        BigDecimal expected = amount.multiply(rate.getPrice());

        CurrencyRatesCache cache = new CurrencyRatesCache();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        RatesConvertingSupplier ratesConvertingSupplier = new RatesConvertingSupplier(cache, executorService);
        CacheUpdateProgram cacheUpdateProgram = new CacheUpdateProgram(executorService, 0L, 1L, TimeUnit.MINUTES);
        CryptoCurrencyRatesCacheUpdater cryptoCurrencyRatesCacheUpdater = new CryptoCurrencyRatesCacheUpdater(cache, binanceTargetRatesSupplier, cacheUpdateProgram);

        given(binanceTargetRatesSupplier.getRatesFromTarget()).willReturn(CompletableFuture.completedFuture(List.of(rate)));

        cryptoCurrencyRatesCacheUpdater.startProgram();

        assertEquals(rate, ratesConvertingSupplier.getRateAsynchronously(asset, quotable, 2).get());
        assertEquals(rate, ratesConvertingSupplier.getRateAsynchronously(asset, quotable).get());
        assertEquals(rate, ratesConvertingSupplier.getRate(asset, quotable).orElseThrow());
        assertThat(expected, comparesEqualTo(ratesConvertingSupplier.convertAsynchronously(amount, asset, quotable).get()));
        assertThat(expected, comparesEqualTo(ratesConvertingSupplier.convertAsynchronously(amount, asset, quotable, 2).get()));
        assertThat(expected, comparesEqualTo(ratesConvertingSupplier.convert(amount, asset, quotable).orElseThrow()));
    }

}
