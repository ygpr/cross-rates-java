package io.github.rates.communicators.monobank;

import static java.util.stream.Collectors.toList;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import io.github.rates.domain.Rate;
import io.github.rates.tools.currency.ISO4217CodeToCurrencyTicker;

import java.util.List;

class MonobankResponseToModelMapper {

    private static final String EMPTY_CURRENCY_NAME = "";
    private final ISO4217CodeToCurrencyTicker iso4217CodeToCurrencyTicker;
    private final MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator;

    public MonobankResponseToModelMapper(
            ISO4217CodeToCurrencyTicker iso4217CodeToCurrencyTicker,
            MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator
    ) {
        this.iso4217CodeToCurrencyTicker = iso4217CodeToCurrencyTicker;
        this.ratePriceFromResponseCalculator = ratePriceFromResponseCalculator;
    }

    List<Rate> mapToRate(List<MonobankRateResponse> monobankRateResponses) {
        return monobankRateResponses.stream()
                .map(this::mapToRate)
                .filter(this::isBothCurrenciesPresent)
                .collect(toList());
    }

    private Rate mapToRate(MonobankRateResponse monobankRateResponse) {
        return new Rate(
                getCurrencyName(monobankRateResponse.getCurrencyCodeA()),
                getCurrencyName(monobankRateResponse.getCurrencyCodeB()),
                ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse)
        );
    }

    private boolean isBothCurrenciesPresent(Rate rate) {
        return !rate.getAsset().isEmpty() && !rate.getQuotable().isEmpty();
    }

    private String getCurrencyName(Integer currencyCode) {
        return iso4217CodeToCurrencyTicker
                .getCurrencyTickerByCode(currencyCode)
                .orElse(EMPTY_CURRENCY_NAME);
    }

}
