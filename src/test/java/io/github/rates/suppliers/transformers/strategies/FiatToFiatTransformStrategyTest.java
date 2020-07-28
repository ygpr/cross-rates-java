package io.github.rates.suppliers.transformers.strategies;

import static io.github.rates.suppliers.transformers.TransformStrategyType.FIAT_TO_FIAT;
import static io.github.rates.suppliers.transformers.strategies.TransformOperations.UKRAINIAN_HRYVNIA_TICKER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import io.github.rates.domain.Rate;
import io.github.rates.tools.math.CurrencyConvertingDecimal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class FiatToFiatTransformStrategyTest {

    @Mock
    private TransformOperations transformOperations;

    @InjectMocks
    private FiatToFiatTransformStrategy fiatToFiatTransformStrategy;

    @Test
    void transform() {
        String currencyFrom = "MNT";
        String currencyTo = "PLN";
        BigDecimal amount = BigDecimal.valueOf(456.123);
        BigDecimal mntToUAHPrice = BigDecimal.valueOf(100);
        BigDecimal plnToUAHPrice = BigDecimal.valueOf(8.2);
        BigDecimal mntToUAH = amount.multiply(mntToUAHPrice);
        BigDecimal converted = CurrencyConvertingDecimal.from(mntToUAH).divideWithDefaultScaling(plnToUAHPrice);

        Rate mntToUAHRate = new Rate("UAH", "MNT", "UAHMNT", 2, 2, mntToUAHPrice);
        Rate uahToPlnRate = new Rate("UAH", "PLN", "UAPLN", 2, 2, plnToUAHPrice);

        given(transformOperations.getRate(currencyFrom, UKRAINIAN_HRYVNIA_TICKER))
                .willReturn(Optional.of(mntToUAHRate));
        given(transformOperations.getRate(currencyTo, UKRAINIAN_HRYVNIA_TICKER))
                .willReturn(Optional.of(uahToPlnRate));

        Optional<BigDecimal> result = fiatToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(converted, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyFromIsUAH() {
        String currencyFrom = UKRAINIAN_HRYVNIA_TICKER;
        String currencyTo = "EGP";
        BigDecimal amount = BigDecimal.valueOf(32.11);
        BigDecimal egpToUAHPrice = BigDecimal.valueOf(8.2);
        BigDecimal converted = CurrencyConvertingDecimal.from(amount).divideWithDefaultScaling(egpToUAHPrice);

        Rate uahToEgpRate = new Rate("UAH", "EGP", "UAHEGP", 2, 2, egpToUAHPrice);

        given(transformOperations.getRate(currencyTo, UKRAINIAN_HRYVNIA_TICKER))
                .willReturn(Optional.of(uahToEgpRate));

        Optional<BigDecimal> result = fiatToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(converted, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyToIsUAH() {
        String currencyFrom = "MNT";
        String currencyTo = UKRAINIAN_HRYVNIA_TICKER;
        BigDecimal amount = BigDecimal.valueOf(456.123);
        BigDecimal mntToUAHPrice = BigDecimal.valueOf(100);
        BigDecimal mntToUAH = amount.multiply(mntToUAHPrice);

        Rate mntToUAHRate = new Rate("UAH", "MNT", "UAHMNT", 2, 2, mntToUAHPrice);

        given(transformOperations.getRate(currencyFrom, UKRAINIAN_HRYVNIA_TICKER))
                .willReturn(Optional.of(mntToUAHRate));


        Optional<BigDecimal> result = fiatToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(mntToUAH, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyFromEqualToCurrencyTo() {
        String currencyFrom = "VND";
        String currencyTo = "VND";
        BigDecimal amount = BigDecimal.valueOf(235);

        Optional<BigDecimal> result = fiatToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(amount, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_withoutResult() {
        assertTrue(fiatToFiatTransformStrategy.transform(BigDecimal.ONE, "IDK", "FA").isEmpty());
    }

    @Test
    void transformAsynchronously() throws Exception {
        String currencyFrom = "MNT";
        String currencyTo = UKRAINIAN_HRYVNIA_TICKER;
        BigDecimal amount = BigDecimal.valueOf(456.123);
        BigDecimal mntToUAHPrice = BigDecimal.valueOf(100);
        BigDecimal mntToUAH = amount.multiply(mntToUAHPrice);

        Rate mntToUAHRate = new Rate("UAH", "MNT", "UAHMNT", 2, 2, mntToUAHPrice);

        given(transformOperations.getRate(currencyFrom, UKRAINIAN_HRYVNIA_TICKER))
                .willReturn(Optional.of(mntToUAHRate));


        BigDecimal result = fiatToFiatTransformStrategy.transformAsynchronously(amount, currencyFrom, currencyTo)
                .get(5, TimeUnit.SECONDS);

        assertThat(mntToUAH, Matchers.comparesEqualTo(result));
    }

    @Test
    void transformAsynchronously_shouldThrowExecutionException() {
        assertThrows(ExecutionException.class,
                () -> fiatToFiatTransformStrategy
                        .transformAsynchronously(BigDecimal.ONE, "IDD", "QD")
                        .get()
        );
    }

    @Test
    void getType() {
        assertEquals(FIAT_TO_FIAT, fiatToFiatTransformStrategy.getType());
    }
}
