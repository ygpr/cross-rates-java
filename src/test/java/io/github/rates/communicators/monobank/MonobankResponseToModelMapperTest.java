package io.github.rates.communicators.monobank;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import io.github.rates.domain.Rate;
import io.github.rates.tools.currency.ISO4217CodeToCurrency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MonobankResponseToModelMapperTest {

    @Mock
    private MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator;

    @Mock
    private ISO4217CodeToCurrency codeToCurrencyTicker;

    @InjectMocks
    private MonobankResponseToModelMapper monobankResponseToModelMapper;

    @Test
    void mapToRate() {
        Integer assetCode = 11414;
        Integer quotableCode = 66262;
        Currency asset = Currency.getInstance("USD");
        Currency quotable = Currency.getInstance("UAH");
        BigDecimal expectedPrice = BigDecimal.valueOf(2857.363);
        MonobankRateResponse monobankRateResponse = createMonobankRateResponse(assetCode, quotableCode, expectedPrice);

        given(codeToCurrencyTicker.getCurrencyTickerByCode(assetCode)).willReturn(Optional.of(asset));
        given(codeToCurrencyTicker.getCurrencyTickerByCode(quotableCode)).willReturn(Optional.of(quotable));
        given(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)).willReturn(expectedPrice);

        List<Rate> rates = monobankResponseToModelMapper.mapToRate(List.of(monobankRateResponse));

        assertNotNull(rates);
        assertFalse(rates.isEmpty());
        assertEquals(1, rates.size());
        assertEquals(asset.getCurrencyCode(), rates.get(0).getAsset());
        assertEquals(quotable.getCurrencyCode(), rates.get(0).getQuotable());
        assertEquals(asset.getDefaultFractionDigits(), rates.get(0).getAssetPrecision());
        assertEquals(quotable.getDefaultFractionDigits(), rates.get(0).getQuotablePrecision());
        assertEquals(asset.getCurrencyCode() + quotable.getCurrencyCode(), rates.get(0).getPairName());
        assertThat(expectedPrice, comparesEqualTo(rates.get(0).getPrice()));
    }

    @Test
    void mapToRate_assetIsNotPresent_shouldReturnEmpty() {
        Integer assetCode = 11414;
        Integer quotableCode = 66262;
        Currency quotable = Currency.getInstance("UAH");
        BigDecimal expectedPrice = BigDecimal.valueOf(2857.363);
        MonobankRateResponse monobankRateResponse = createMonobankRateResponse(assetCode, quotableCode, expectedPrice);

        given(codeToCurrencyTicker.getCurrencyTickerByCode(assetCode)).willReturn(Optional.empty());
        given(codeToCurrencyTicker.getCurrencyTickerByCode(quotableCode)).willReturn(Optional.of(quotable));
        given(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)).willReturn(expectedPrice);

        assertTrue(monobankResponseToModelMapper.mapToRate(List.of(monobankRateResponse)).isEmpty());
    }

    @Test
    void mapToRate_quotableIsNotPresent_shouldReturnEmpty() {
        Integer assetCode = 11414;
        Integer quotableCode = 66262;
        Currency asset = Currency.getInstance("USD");
        BigDecimal expectedPrice = BigDecimal.valueOf(2857.363);
        MonobankRateResponse monobankRateResponse = createMonobankRateResponse(assetCode, quotableCode, expectedPrice);

        given(codeToCurrencyTicker.getCurrencyTickerByCode(assetCode)).willReturn(Optional.of(asset));
        given(codeToCurrencyTicker.getCurrencyTickerByCode(quotableCode)).willReturn(Optional.empty());
        given(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)).willReturn(expectedPrice);

        assertTrue(monobankResponseToModelMapper.mapToRate(List.of(monobankRateResponse)).isEmpty());
    }

    @Test
    void mapToRate_bothCurrenciesIsNotPresent_shouldReturnEmpty() {
        Integer assetCode = 11414;
        Integer quotableCode = 66262;
        BigDecimal expectedPrice = BigDecimal.valueOf(2857.363);
        MonobankRateResponse monobankRateResponse = createMonobankRateResponse(assetCode, quotableCode, expectedPrice);

        given(codeToCurrencyTicker.getCurrencyTickerByCode(assetCode)).willReturn(Optional.empty());
        given(codeToCurrencyTicker.getCurrencyTickerByCode(quotableCode)).willReturn(Optional.empty());
        given(ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)).willReturn(expectedPrice);

        assertTrue(monobankResponseToModelMapper.mapToRate(List.of(monobankRateResponse)).isEmpty());
    }

    private MonobankRateResponse createMonobankRateResponse(Integer assetCode, Integer quotableCode, BigDecimal expectedPrice) {
        return new MonobankRateResponse(
                assetCode,
                quotableCode,
                expectedPrice,
                expectedPrice,
                expectedPrice,
                System.currentTimeMillis()
        );
    }

}
