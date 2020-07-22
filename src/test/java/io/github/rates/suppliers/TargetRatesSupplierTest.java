package io.github.rates.suppliers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.RatesJsonSupplier;
import io.github.rates.model.Rate;
import io.github.rates.parsers.RatesJsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TargetRatesSupplierTest {

    @Mock
    private RatesJsonSupplier ratesJsonSupplier;

    @Mock
    private RatesJsonParser ratesJsonParser;

    @InjectMocks
    private AbstractTargetRatesSupplier ratesSupplier;

    @Test
    void getRatesFromTarget() {
        Rate rate = new Rate("btceth", BigDecimal.ONE);
        String json = "{\"pairName\":\"btceth\",\"price\":\"1\"}";

        given(ratesJsonSupplier.getRatesAsJson()).willReturn(json);
        given(ratesJsonParser.parseJson(json)).willReturn(List.of(rate));

        assertEquals(List.of(rate), ratesSupplier.getRatesFromTarget());
    }

}
