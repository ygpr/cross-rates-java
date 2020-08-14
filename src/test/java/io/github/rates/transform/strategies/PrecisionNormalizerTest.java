package io.github.rates.transform.strategies;

import static io.github.rates.transform.strategies.TransformOperations.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.tools.currency.ISO4217CodeToCurrency;
import io.github.rates.tools.math.CurrencyConvertingDecimal;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Disabled
@ExtendWith(MockitoExtension.class)
class PrecisionNormalizerTest {

    @Mock
    private ISO4217CodeToCurrency iso4217CodeToCurrency;

    @Mock
    private RatesSupplier ratesSupplier;

    @InjectMocks
    private PrecisionNormalizer precisionNormalizer;

    @Test
    void normalize_fiat() {
        int precision = 4;
        BigDecimal amount = BigDecimal.valueOf(1.2425);
        String sourceCurrency = "PLN";
        Rate rate = new Rate(sourceCurrency, UKRAINIAN_HRYVNIA_TICKER, "m", precision, precision, BigDecimal.ONE);

        given(iso4217CodeToCurrency.isFiat(sourceCurrency)).willReturn(true);
        given(ratesSupplier.getRate(sourceCurrency, UKRAINIAN_HRYVNIA_TICKER)).willReturn(Optional.of(rate));

        BigDecimal result = precisionNormalizer.normalize(CurrencyConvertingDecimal.from(amount), sourceCurrency);

        assertThat(amount, comparesEqualTo(result));
        assertEquals(amount.precision(), result.precision());
    }

    @Test
    void normalize_fiat_sourceIsUAH() {
        int precision = 2;
        BigDecimal amount = BigDecimal.valueOf(5.45);
        String sourceCurrency = "UAH";
        Rate rate = new Rate(USD_TICKER, UKRAINIAN_HRYVNIA_TICKER, "m", precision, precision, BigDecimal.ONE);

        given(iso4217CodeToCurrency.isFiat(sourceCurrency)).willReturn(true);
        given(ratesSupplier.getRate(USD_TICKER, UKRAINIAN_HRYVNIA_TICKER)).willReturn(Optional.of(rate));

        BigDecimal result = precisionNormalizer.normalize(CurrencyConvertingDecimal.from(amount), sourceCurrency);

        assertThat(amount, comparesEqualTo(result));
        assertEquals(amount.precision(), result.precision());
    }

    @Test
    void normalize_fiat_notFoundSource() {
        BigDecimal amount = BigDecimal.valueOf(5.45);
        String sourceCurrency = "UAH";

        given(iso4217CodeToCurrency.isFiat(sourceCurrency)).willReturn(true);
        given(ratesSupplier.getRate(USD_TICKER, UKRAINIAN_HRYVNIA_TICKER)).willReturn(Optional.empty());

        BigDecimal result = precisionNormalizer.normalize(CurrencyConvertingDecimal.from(amount), sourceCurrency);

        assertThat(CurrencyConvertingDecimal.from(amount), comparesEqualTo(result));
        assertEquals(CurrencyConvertingDecimal.from(amount).precision(), result.precision());
    }

    @Test
    void normalize_crypto() {
        int precision = 3;
        BigDecimal amount = BigDecimal.valueOf(23.234000000234);
        BigDecimal expectedAmount = amount.setScale(3, RoundingMode.HALF_UP);
        String sourceCurrency = BITCOIN_TICKER;
        Rate rate = new Rate(BITCOIN_TICKER, USD_TETHER_TICKER, "m", precision, precision, BigDecimal.ONE);

        given(iso4217CodeToCurrency.isFiat(sourceCurrency)).willReturn(false);
        given(ratesSupplier.getRate(sourceCurrency, ETHERIUM_TICKER)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(sourceCurrency, BITCOIN_TICKER)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(sourceCurrency, USD_TETHER_TICKER)).willReturn(Optional.of(rate));

        BigDecimal result = precisionNormalizer.normalize(CurrencyConvertingDecimal.from(amount), sourceCurrency);

        assertThat(expectedAmount, comparesEqualTo(result));
        assertEquals(expectedAmount.precision(), result.precision());
    }

    @Test
    void normalize_crypto_notFoundSource() {
        BigDecimal amount = BigDecimal.valueOf(23.234000000234);
        String sourceCurrency = "LTC";

        given(iso4217CodeToCurrency.isFiat(sourceCurrency)).willReturn(false);
        given(ratesSupplier.getRate(sourceCurrency, ETHERIUM_TICKER)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(sourceCurrency, BITCOIN_TICKER)).willReturn(Optional.empty());
        given(ratesSupplier.getRate(sourceCurrency, USD_TETHER_TICKER)).willReturn(Optional.empty());

        BigDecimal result = precisionNormalizer.normalize(CurrencyConvertingDecimal.from(amount), sourceCurrency);

        assertThat(CurrencyConvertingDecimal.from(amount), comparesEqualTo(result));
        assertEquals(CurrencyConvertingDecimal.from(amount).precision(), result.precision());
    }

}
