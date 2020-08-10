package io.github.rates.transform;

import static io.github.rates.domain.TransformStrategyType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.tools.currency.ISO4217CodeToCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransformStrategyTypeQualifierTest {

    @Mock
    private ISO4217CodeToCurrency iso4217CodeToCurrency;

    private TransformStrategyTypeQualifier transformStrategyTypeQualifier;

    @BeforeEach
    void setUp() {
        transformStrategyTypeQualifier = new TransformStrategyTypeQualifier(
                iso4217CodeToCurrency,
                TransformStrategyTypeQualifier.getResultToTypeMap()
        );
    }

    @Test
    void getType_fiatToFiat() {
        String currencyFrom = "UAH";
        String currencyTo = "USD";

        given(iso4217CodeToCurrency.isFiat(currencyFrom)).willReturn(true);
        given(iso4217CodeToCurrency.isFiat(currencyTo)).willReturn(true);

        assertEquals(FIAT_TO_FIAT, transformStrategyTypeQualifier.getType(currencyFrom, currencyTo));
    }

    @Test
    void getType_fiatToCrypto() {
        String currencyFrom = "UAH";
        String currencyTo = "BTC";

        given(iso4217CodeToCurrency.isFiat(currencyFrom)).willReturn(true);
        given(iso4217CodeToCurrency.isFiat(currencyTo)).willReturn(false);

        assertEquals(FIAT_TO_CRYPTO, transformStrategyTypeQualifier.getType(currencyFrom, currencyTo));
    }

    @Test
    void getType_cryptoToFiat() {
        String currencyFrom = "ETH";
        String currencyTo = "JPY";

        given(iso4217CodeToCurrency.isFiat(currencyFrom)).willReturn(false);
        given(iso4217CodeToCurrency.isFiat(currencyTo)).willReturn(true);

        assertEquals(CRYPTO_TO_FIAT, transformStrategyTypeQualifier.getType(currencyFrom, currencyTo));
    }

    @Test
    void getType_cryptoToCrypto() {
        String currencyFrom = "DOGE";
        String currencyTo = "LTC";

        given(iso4217CodeToCurrency.isFiat(currencyFrom)).willReturn(false);
        given(iso4217CodeToCurrency.isFiat(currencyTo)).willReturn(false);

        assertEquals(CRYPTO_TO_CRYPTO, transformStrategyTypeQualifier.getType(currencyFrom, currencyTo));
    }
}
