package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.CRYPTO_TO_FIAT;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;
import static io.github.rates.transform.strategies.TransformOperations.EURO_TICKER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

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

@ExtendWith({MockitoExtension.class})
class CryptoToFiatTransformStrategyTest {

    @Mock
    private TransformOperations transformOperations;

    @Mock
    private FiatToFiatTransformStrategy fiatToFiatTransformStrategy;

    @InjectMocks
    private CryptoToFiatTransformStrategy cryptoToFiatTransformStrategy;

    @Test
    void transform() {
        String currencyFrom = "DASH";
        String currencyTo = "USD";
        BigDecimal amount = BigDecimal.valueOf(1);
        BigDecimal dashToBTC = BigDecimal.valueOf(0.0014124);
        BigDecimal btcToEur = BigDecimal.valueOf(13);
        BigDecimal dashInEur = CurrencyConvertingDecimal.from(dashToBTC).multiply(btcToEur);
        BigDecimal dashInUSD = BigDecimal.valueOf(1.000001223424423);

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER))
                .willReturn(Optional.of(dashToBTC));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEur));
        given(fiatToFiatTransformStrategy.transform(dashInEur, EURO_TICKER, currencyTo))
                .willReturn(Optional.of(dashInUSD));

        Optional<BigDecimal> result = cryptoToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(dashInUSD, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyFromIsBTC() {
        String currencyFrom = BITCOIN_TICKER;
        String currencyTo = "USD";
        BigDecimal amount = BigDecimal.valueOf(1);
        BigDecimal btcToEurRate = BigDecimal.valueOf(3242);
        BigDecimal btcToEur = CurrencyConvertingDecimal.from(amount).multiply(btcToEurRate);
        BigDecimal btcInUSD = BigDecimal.valueOf(9000);

        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEurRate));
        given(fiatToFiatTransformStrategy.transform(btcToEur, EURO_TICKER, currencyTo))
                .willReturn(Optional.of(btcInUSD));

        Optional<BigDecimal> result = cryptoToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(btcInUSD, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyToIsEuro() {
        String currencyFrom = "DASH";
        String currencyTo = "EURO";
        BigDecimal amount = BigDecimal.valueOf(1);
        BigDecimal dashToBTC = BigDecimal.valueOf(0.0014124);
        BigDecimal btcToEur = BigDecimal.valueOf(13);
        BigDecimal dashInEur = CurrencyConvertingDecimal.from(dashToBTC).multiply(btcToEur);
        BigDecimal dashInUSD = BigDecimal.valueOf(1.000001223424423);

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER))
                .willReturn(Optional.of(dashToBTC));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEur));
        given(fiatToFiatTransformStrategy.transform(dashInEur, EURO_TICKER, currencyTo))
                .willReturn(Optional.of(dashInUSD));

        Optional<BigDecimal> result = cryptoToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(dashInUSD, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_BitcoinTransformingToEur() {
        String currencyFrom = BITCOIN_TICKER;
        String currencyTo = EURO_TICKER;
        BigDecimal amount = BigDecimal.valueOf(1);
        BigDecimal btcInEurRate = BigDecimal.valueOf(2342424);
        BigDecimal btcConverted = CurrencyConvertingDecimal.from(amount).multiply(btcInEurRate);

        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcInEurRate));

        Optional<BigDecimal> result = cryptoToFiatTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(btcConverted, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_withoutResult() {
        assertTrue(cryptoToFiatTransformStrategy.transform(BigDecimal.ONE, "TEST", "TEST").isEmpty());
    }

    @Test
    void transformAsynchronously() throws Exception {
        String currencyFrom = BITCOIN_TICKER;
        String currencyTo = EURO_TICKER;
        BigDecimal amount = BigDecimal.valueOf(1);
        BigDecimal btcInEurRate = BigDecimal.valueOf(2342424);
        BigDecimal btcConverted = CurrencyConvertingDecimal.from(amount).multiply(btcInEurRate);

        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcInEurRate));


        BigDecimal result = cryptoToFiatTransformStrategy.transformAsynchronously(amount, currencyFrom, currencyTo)
                .get(5, TimeUnit.SECONDS);

        assertThat(btcConverted, Matchers.comparesEqualTo(result));
    }

    @Test
    void transformAsynchronously_shouldThrowExecutionException() {
        assertThrows(ExecutionException.class,
                () -> cryptoToFiatTransformStrategy
                        .transformAsynchronously(BigDecimal.ONE, "test", "test")
                        .get()
        );
    }

    @Test
    void getType() {
        assertEquals(CRYPTO_TO_FIAT, cryptoToFiatTransformStrategy.getType());
    }
}
