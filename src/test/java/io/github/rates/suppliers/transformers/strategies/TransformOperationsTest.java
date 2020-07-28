package io.github.rates.suppliers.transformers.strategies;

import static io.github.rates.suppliers.transformers.strategies.TransformOperations.USD_TETHER_TICKER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.tools.math.CurrencyConvertingDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransformOperationsTest {

    @Mock
    private RatesSupplier ratesSupplier;

    @InjectMocks
    private TransformOperations transformOperations;

    @Test
    void transformCryptoCurrencies_convertDirectly() {
        String from = "NZD";
        String to = "UAH";
        BigDecimal amount = BigDecimal.valueOf(12);
        BigDecimal currentRate = BigDecimal.valueOf(23.24);
        BigDecimal nazToUah = CurrencyConvertingDecimal.from(amount).multiply(currentRate);
        Rate rate = new Rate(from, to, from + to, 1, 1, currentRate);

        given(ratesSupplier.getRate(from, to)).willReturn(Optional.of(rate));

        Optional<BigDecimal> result = transformOperations.transformCryptoCurrencies(amount, from, to);

        assertTrue(result.isPresent());
        assertThat(nazToUah, comparesEqualTo(result.get()));
    }

    @Test
    void transformCryptoCurrencies_getUnitOfPriceBySwapping() {
        String from = "NZD";
        String to = "UAH";
        BigDecimal amount = BigDecimal.valueOf(12);
        BigDecimal currentRate = BigDecimal.valueOf(23.24);
        BigDecimal uahToNaz = CurrencyConvertingDecimal.from(BigDecimal.ONE).divideWithDefaultScaling(currentRate);
        BigDecimal converted = CurrencyConvertingDecimal.from(uahToNaz).multiply(amount);
        Rate rate = new Rate(to, from, to + from, 1, 1, currentRate);

        given(ratesSupplier.getRate(from, to)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(to, from)).willReturn(Optional.of(rate));

        Optional<BigDecimal> result = transformOperations.transformCryptoCurrencies(amount, from, to);

        assertTrue(result.isPresent());
        assertThat(converted, comparesEqualTo(result.get()));
    }

    @Test
    void transformCryptoCurrencies_bySwappingCurrencies() {
        String from = "NZD";
        String to = "UAH";
        BigDecimal amount = BigDecimal.valueOf(12);
        BigDecimal currentRate = BigDecimal.valueOf(23.24);
        BigDecimal uahToNaz = CurrencyConvertingDecimal.from(amount).divideWithDefaultScaling(currentRate);
        Rate rate = new Rate(to, from, to + from, 1, 1, currentRate);

        given(ratesSupplier.getRate(from, to)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(to, from)).willReturn(Optional.empty()).willReturn(Optional.of(rate));
        given(ratesSupplier.getRate(from, USD_TETHER_TICKER)).willReturn(Optional.empty());

        Optional<BigDecimal> result = transformOperations.transformCryptoCurrencies(amount, from, to);

        assertTrue(result.isPresent());
        assertThat(uahToNaz, comparesEqualTo(result.get()));
    }

    @Test
    void transformCryptoCurrencies_getPriceViaTether() {
        String from = "NZD";
        String to = "UAH";
        BigDecimal amount = BigDecimal.valueOf(12);
        BigDecimal currentRate = BigDecimal.valueOf(23.24);
        BigDecimal tetherRate = CurrencyConvertingDecimal
                .from(currentRate)
                .divideWithDefaultScaling(currentRate)
                .multiply(amount);

        Rate rate = new Rate(to, from, to + from, 1, 1, currentRate);

        given(ratesSupplier.getRate(from, to)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(to, from)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(from, USD_TETHER_TICKER)).willReturn(Optional.of(rate));
        given(ratesSupplier.getRate(USD_TETHER_TICKER, to)).willReturn(Optional.of(rate));

        Optional<BigDecimal> result = transformOperations.transformCryptoCurrencies(amount, from, to);

        assertTrue(result.isPresent());
        assertThat(tetherRate, comparesEqualTo(result.get()));
    }

}
