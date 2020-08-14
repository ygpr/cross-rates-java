package io.github.rates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.rates.domain.Rate;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Optional;

@Disabled
class DefaultCrossRatesAPITest {

    @Test
    void mainScenarioTest() {
        CrossRatesAPI crossRatesAPI = new CrossRatesApiFactory().buildDefault();
        Awaitility.await().atMost(Duration.ofSeconds(5)).until(() -> !crossRatesAPI.getCryptoCurrencies().isEmpty()
                && !crossRatesAPI.getFiatCurrencies().isEmpty());

        Optional<Rate> oRate = crossRatesAPI.getRate("AFN", "ADA");
        assertTrue(oRate.isPresent());
        Rate rate = oRate.get();
        assertNotNull(rate.getPrice());
        assertTrue(rate.getPrice().compareTo(BigDecimal.ZERO) > 0);
        System.out.printf("Pair %s has rate %s%n", rate.getPairName(), rate.getPrice());
        Optional<Rate> oRate1 = crossRatesAPI.getRate("ADA", "AFN");
        assertTrue(oRate1.isPresent());
        Rate rate1 = oRate1.get();
        assertNotNull(rate1.getPrice());
        assertTrue(rate1.getPrice().compareTo(BigDecimal.ZERO) > 0);
        System.out.printf("Pair %s has rate %s%n", rate1.getPairName(), rate1.getPrice());
        BigDecimal formattedPrice = rate1.getPrice().setScale(8, RoundingMode.HALF_UP);
        BigDecimal expectedPrice = BigDecimal.ONE.divide(rate.getPrice(), MathContext.DECIMAL64)
                .setScale(8, RoundingMode.HALF_UP);
        assertEquals(formattedPrice, expectedPrice);
    }
}
