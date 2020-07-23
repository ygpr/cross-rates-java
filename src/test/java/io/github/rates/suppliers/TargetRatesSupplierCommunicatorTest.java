package io.github.rates.suppliers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.RatesSupplierCommunicator;
import io.github.rates.domain.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class TargetRatesSupplierCommunicatorTest {

    @Mock
    private RatesSupplierCommunicator ratesSupplierCommunicator;

    @InjectMocks
    private AbstractTargetRatesSupplier ratesSupplier;

    @Test
    void getRatesFromTarget() throws Exception {
        Rate rate = new Rate("DASH", "XRP", BigDecimal.ONE);

        given(ratesSupplierCommunicator.getRates()).willReturn(CompletableFuture.completedFuture(List.of(rate)));

        assertEquals(List.of(rate), ratesSupplier.getRatesFromTarget().get());
    }

}
