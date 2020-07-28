package io.github.rates.transform.strategies;

import static io.github.rates.domain.TransformStrategyType.CRYPTO_TO_CRYPTO;
import static io.github.rates.transform.strategies.TransformOperations.BITCOIN_TICKER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

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
class CryptoToCryptoTransformStrategyTest {

    @Mock
    private TransformOperations transformOperations;

    @InjectMocks
    private CryptoToCryptoTransformStrategy cryptoToCryptoTransformStrategy;

    @Test
    void transform_directly() {
        String currencyFrom = "ETH";
        String currencyTo = "BTC";
        BigDecimal amount = BigDecimal.valueOf(32);
        BigDecimal converted = BigDecimal.ONE;

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo))
                .willReturn(Optional.of(converted));

        Optional<BigDecimal> result = cryptoToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(converted, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_viaBTC() {
        String currencyFrom = "ADA";
        String currencyTo = "ZEC";
        BigDecimal amount = BigDecimal.valueOf(9);
        BigDecimal currencyFromInBTC = BigDecimal.valueOf(44);
        BigDecimal currencyToAmount = BigDecimal.valueOf(362235);

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo))
                .willReturn(Optional.empty());
        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER))
                .willReturn(Optional.of(currencyFromInBTC));
        given(transformOperations.transformCryptoCurrencies(currencyFromInBTC, BITCOIN_TICKER, currencyTo))
                .willReturn(Optional.of(currencyToAmount));

        Optional<BigDecimal> result = cryptoToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo);

        assertTrue(result.isPresent());
        assertThat(currencyToAmount, Matchers.comparesEqualTo(result.get()));
    }

    @Test
    void transform_withoutResult() {
        String currencyFrom = "TEST";
        String currencyTo = "TEST";
        BigDecimal amount = BigDecimal.valueOf(32);

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo))
                .willReturn(Optional.empty());

        assertTrue( cryptoToCryptoTransformStrategy.transform(amount, currencyFrom, currencyTo).isEmpty());
    }


    @Test
    void transformAsynchronously() throws Exception {
        String currencyFrom = "ADA";
        String currencyTo = "ZEC";
        BigDecimal amount = BigDecimal.valueOf(9);
        BigDecimal currencyFromInBTC = BigDecimal.valueOf(44);
        BigDecimal currencyToAmount = BigDecimal.valueOf(362235);

        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, currencyTo))
                .willReturn(Optional.empty());
        given(transformOperations.transformCryptoCurrencies(amount, currencyFrom, BITCOIN_TICKER))
                .willReturn(Optional.of(currencyFromInBTC));
        given(transformOperations.transformCryptoCurrencies(currencyFromInBTC, BITCOIN_TICKER, currencyTo))
                .willReturn(Optional.of(currencyToAmount));

        BigDecimal result = cryptoToCryptoTransformStrategy.transformAsynchronously(amount, currencyFrom, currencyTo)
                .get(5, TimeUnit.SECONDS);

        assertThat(currencyToAmount, Matchers.comparesEqualTo(result));
    }

    @Test
    void transformAsynchronously_shouldThrowExecutionException() {
        assertThrows(ExecutionException.class,
                () -> cryptoToCryptoTransformStrategy
                        .transformAsynchronously(BigDecimal.ONE, "test", "test")
                        .get()
        );
    }

    @Test
    void getType() {
        assertEquals(CRYPTO_TO_CRYPTO, cryptoToCryptoTransformStrategy.getType());
    }
}
