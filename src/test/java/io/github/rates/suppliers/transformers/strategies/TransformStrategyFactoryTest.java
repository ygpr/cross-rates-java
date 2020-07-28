package io.github.rates.suppliers.transformers.strategies;

import static io.github.rates.suppliers.transformers.TransformStrategyType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.rates.suppliers.RatesSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransformStrategyFactoryTest {

    @Mock
    private RatesSupplier ratesSupplier;

    @InjectMocks
    private TransformStrategyFactory transformStrategyFactory;

    @Test
    void getFiatToFiatTransformStrategy() {
        assertEquals(FIAT_TO_FIAT, transformStrategyFactory.getFiatToFiatTransformStrategy().getType());
    }

    @Test
    void getCryptoToCryptoTransformStrategy() {
        assertEquals(CRYPTO_TO_CRYPTO, transformStrategyFactory.getCryptoToCryptoTransformStrategy().getType());
    }

    @Test
    void getCryptoToFiatTransformStrategy() {
        assertEquals(CRYPTO_TO_FIAT, transformStrategyFactory.getCryptoToFiatTransformStrategy().getType());
    }

    @Test
    void getFiatToCryptoTransformStrategy() {
        assertEquals(FIAT_TO_CRYPTO, transformStrategyFactory.getFiatToCryptoTransformStrategy().getType());
    }

    @Test
    void getTransformStrategiesAsMap() {
        assertEquals(FIAT_TO_FIAT, transformStrategyFactory.getTransformStrategiesAsMap().get(FIAT_TO_FIAT).getType());
        assertEquals(CRYPTO_TO_CRYPTO, transformStrategyFactory.getTransformStrategiesAsMap().get(CRYPTO_TO_CRYPTO).getType());
        assertEquals(CRYPTO_TO_FIAT, transformStrategyFactory.getTransformStrategiesAsMap().get(CRYPTO_TO_FIAT).getType());
        assertEquals(FIAT_TO_CRYPTO, transformStrategyFactory.getTransformStrategiesAsMap().get(FIAT_TO_CRYPTO).getType());
    }
}
