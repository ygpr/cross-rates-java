package io.github.rates.suppliers.transformers.strategies;

import static io.github.rates.suppliers.transformers.TransformStrategyType.FIAT_TO_CRYPTO;
import static io.github.rates.suppliers.transformers.strategies.TransformOperations.BITCOIN_TICKER;
import static io.github.rates.suppliers.transformers.strategies.TransformOperations.EURO_TICKER;
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

@ExtendWith(MockitoExtension.class)
class FiatToCryptoTransformStrategyTest {

    @Mock
    private TransformOperations transformOperations;

    @Mock
    private FiatToFiatTransformStrategy fiatToFiatTransformStrategy;

    @Mock
    private CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategy;

    @InjectMocks
    private FiatToCryptoTransformStrategy fiatToCryptoTransformStrategy;

    @Test
    void transform() {
        String currencyFrom = "MNT";
        String currencyTo = "XRP";
        BigDecimal amount = BigDecimal.valueOf(23.2342);
        BigDecimal currencyFromToEur = BigDecimal.valueOf(44);
        BigDecimal btcToEuroPrice = BigDecimal.valueOf(0.12313);
        BigDecimal btcAmount = new CurrencyConvertingDecimal(currencyFromToEur).divideWithDefaultScaling(btcToEuroPrice);
        BigDecimal btcInXRPAmount = BigDecimal.valueOf(1.000001223424423);

        given(fiatToFiatTransformStrategy.transform(amount, currencyFrom, EURO_TICKER))
                .willReturn(Optional.of(currencyFromToEur));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEuroPrice));
        given(cryptoToCryptoTransformStrategy.transform(btcAmount, BITCOIN_TICKER, currencyTo))
                .willReturn(Optional.of(btcInXRPAmount));

        Optional<BigDecimal> result = fiatToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(btcInXRPAmount, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyFromIsEuro() {
        String currencyFrom = EURO_TICKER;
        String currencyTo = "XRP";
        BigDecimal amount = BigDecimal.valueOf(23.2342);
        BigDecimal btcToEuroPrice = BigDecimal.valueOf(0.12313);
        BigDecimal btcAmount = new CurrencyConvertingDecimal(amount).divideWithDefaultScaling(btcToEuroPrice);
        BigDecimal btcInXRPAmount = BigDecimal.valueOf(1.000001223424423);

        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEuroPrice));
        given(cryptoToCryptoTransformStrategy.transform(btcAmount, BITCOIN_TICKER, currencyTo))
                .willReturn(Optional.of(btcInXRPAmount));

        Optional<BigDecimal> result = fiatToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(btcInXRPAmount, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_currencyToIsBTC() {
        String currencyFrom = "PLN";
        String currencyTo = BITCOIN_TICKER;
        BigDecimal amount = BigDecimal.valueOf(23.2342);
        BigDecimal currencyFromToEur = BigDecimal.valueOf(44);
        BigDecimal btcToEuroPrice = BigDecimal.valueOf(0.12313);
        BigDecimal btcAmount = new CurrencyConvertingDecimal(currencyFromToEur).divideWithDefaultScaling(btcToEuroPrice);

        given(fiatToFiatTransformStrategy.transform(amount, currencyFrom, EURO_TICKER))
                .willReturn(Optional.of(currencyFromToEur));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEuroPrice));

        Optional<BigDecimal> result = fiatToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(btcAmount, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_withoutResult() {
        String currencyFrom = "MNT";
        String currencyTo = "XRP";
        BigDecimal amount = BigDecimal.valueOf(23.2342);
        BigDecimal currencyFromToEur = BigDecimal.valueOf(44);

        given(fiatToFiatTransformStrategy.transform(amount, currencyFrom, EURO_TICKER))
                .willReturn(Optional.of(currencyFromToEur));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.empty());

        assertTrue(fiatToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo).isEmpty());
    }

    @Test
    void transformAsynchronously() throws Exception {
        String currencyFrom = "MNT";
        String currencyTo = "XRP";
        BigDecimal amount = BigDecimal.valueOf(23.2342);
        BigDecimal currencyFromToEur = BigDecimal.valueOf(44);
        BigDecimal btcToEuroPrice = BigDecimal.valueOf(0.12313);
        BigDecimal btcAmount = new CurrencyConvertingDecimal(currencyFromToEur).divideWithDefaultScaling(btcToEuroPrice);
        BigDecimal btcInXRPAmount = BigDecimal.valueOf(1.000001223424423);

        given(fiatToFiatTransformStrategy.transform(amount, currencyFrom, EURO_TICKER))
                .willReturn(Optional.of(currencyFromToEur));
        given(transformOperations.getCryptoPriceOrTetherEquivalent(BITCOIN_TICKER, EURO_TICKER))
                .willReturn(Optional.of(btcToEuroPrice));
        given(cryptoToCryptoTransformStrategy.transform(btcAmount, BITCOIN_TICKER, currencyTo))
                .willReturn(Optional.of(btcInXRPAmount));

        BigDecimal result = fiatToCryptoTransformStrategy.transformAsynchronously(amount, currencyFrom, currencyTo)
                .get(5, TimeUnit.SECONDS);

        assertThat(btcInXRPAmount, Matchers.comparesEqualTo(result));
    }

    @Test
    void transformAsynchronously_shouldThrowExecutionException() {
        assertThrows(ExecutionException.class,
                () -> fiatToCryptoTransformStrategy
                        .transformAsynchronously(BigDecimal.ONE, "test", "test")
                        .get()
        );
    }

    @Test
    void getType() {
        assertEquals(FIAT_TO_CRYPTO, fiatToCryptoTransformStrategy.getType());
    }
}
