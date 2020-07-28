package io.github.rates.suppliers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.RatesProvidingCommunicator;
import io.github.rates.domain.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TargetRatesProvidingCommunicatorTest {

    @Mock
    private RatesProvidingCommunicator ratesProvidingCommunicator;

    private TargetRatesProvider ratesSupplier;

    @BeforeEach
    void setUp() {
        ratesSupplier = new MockedRatesProvider(ratesProvidingCommunicator);
    }

    @Test
    void getRatesFromTarget() throws Exception {
        Rate rate = new Rate("DASH", "XRP", "DASHXRP", 4, 4, BigDecimal.ONE);

        given(ratesProvidingCommunicator.getRates()).willReturn(CompletableFuture.completedFuture(List.of(rate)));

        assertEquals(List.of(rate), ratesSupplier.getRatesFromTarget().get());
    }

    class MockedRatesProvider extends TargetRatesProvider {

        public MockedRatesProvider(RatesProvidingCommunicator ratesProvidingCommunicator) {
            super(ratesProvidingCommunicator);
        }

    }

}
