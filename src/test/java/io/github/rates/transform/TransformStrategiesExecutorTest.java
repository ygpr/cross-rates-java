package io.github.rates.transform;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.rates.domain.TransformStrategyType;
import io.github.rates.transform.strategies.TransformStrategy;
import io.github.rates.transform.strategies.TransformStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TransformStrategiesExecutorTest {

    @Mock
    private TransformStrategy transformStrategy;

    @Mock
    private TransformStrategyFactory transformStrategyFactory;

    @Mock
    private Map<TransformStrategyType, TransformStrategy> transformStrategies;

    private TransformStrategiesExecutor transformStrategiesExecutor;

    @BeforeEach
    void setUp() {
        given(transformStrategyFactory.getTransformStrategiesAsMap()).willReturn(transformStrategies);

        transformStrategiesExecutor = new TransformStrategiesExecutor(transformStrategyFactory);
    }

    @Test
    void transform() {
        String from = "BTC";
        String to = "UAH";
        BigDecimal amount = BigDecimal.ONE;
        BigDecimal transformed = BigDecimal.valueOf(24000);

        given(transformStrategies.get(TransformStrategyType.CRYPTO_TO_FIAT)).willReturn(transformStrategy);
        given(transformStrategy.transform(amount, from, to)).willReturn(Optional.of(transformed));

        Optional<BigDecimal> response = transformStrategiesExecutor.transform(amount, from, to);

        assertTrue(response.isPresent());
        assertThat(transformed, comparesEqualTo(response.get()));
    }

    @Test
    void transform_currenciesEqual() {
        String from = "BTC";
        String to = "BTC";
        BigDecimal amount = BigDecimal.ONE;

        Optional<BigDecimal> response = transformStrategiesExecutor.transform(amount, from, to);

        assertTrue(response.isPresent());
        assertThat(amount, comparesEqualTo(response.get()));

        verify(transformStrategies, never()).get(any());
        verify(transformStrategy, never()).transform(any(), any(), any());
    }

    @Test
    void transformAsync() throws Exception {
        String from = "BTC";
        String to = "UAH";
        BigDecimal amount = BigDecimal.ONE;
        BigDecimal transformed = BigDecimal.valueOf(24000);

        given(transformStrategies.get(TransformStrategyType.CRYPTO_TO_FIAT)).willReturn(transformStrategy);
        given(transformStrategy.transformAsynchronously(amount, from, to)).willReturn(CompletableFuture.completedFuture(transformed));

        CompletableFuture<BigDecimal> response = transformStrategiesExecutor.transformAsync(amount, from, to);

        assertThat(transformed, comparesEqualTo(response.get()));
    }

    @Test
    void transformAsync_currenciesEqual() throws Exception {
        String from = "BTC";
        String to = "BTC";
        BigDecimal amount = BigDecimal.ONE;

        CompletableFuture<BigDecimal> response = transformStrategiesExecutor.transformAsync(amount, from, to);

        assertThat(amount, comparesEqualTo(response.get()));

        verify(transformStrategies, never()).get(any());
        verify(transformStrategy, never()).transformAsynchronously(any(), any(), any());
    }
}
