package io.github.rates.communicators.monobank;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MonobankRatePriceFromResponseCalculatorTest {

    private MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator = new MonobankRatePriceFromResponseCalculator();

    @Test
    void getRatePrice_priceByCross() {
        BigDecimal crossRate = BigDecimal.valueOf(7.40);
        MonobankRateResponse monobankRateResponse = new MonobankRateResponse(
                123,
                431,
                null,
                null,
                crossRate,
                System.currentTimeMillis()
        );

        assertThat(crossRate, comparesEqualTo(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)));
    }

    @Test
    void getRatePrice_priceBySellAndBuy() {
        BigDecimal buyPrice = BigDecimal.TEN;
        BigDecimal sellPrice = BigDecimal.valueOf(8.40);
        BigDecimal expectedPrice = buyPrice.add(sellPrice).divide(BigDecimal.valueOf(2));

        MonobankRateResponse monobankRateResponse = new MonobankRateResponse(
                123,
                431,
                sellPrice,
                buyPrice,
                null,
                System.currentTimeMillis()
        );

        assertThat(expectedPrice, comparesEqualTo(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)));
    }


    @Test
    void getRatePrice_nullValuesInResponse_shouldRethrowRuntimeException() {
        MonobankRateResponse monobankRateResponse = new MonobankRateResponse(
                123,
                431,
                null,
                null,
                null,
                System.currentTimeMillis()
        );

        assertThrows(RuntimeException.class, () -> ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse));
    }
}
