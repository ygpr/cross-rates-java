package io.github.rates.communicators.monobank;

import static java.util.stream.Collectors.toList;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;
import io.github.rates.domain.Rate;
import io.github.rates.tools.currency.ISO4217CodeToCurrency;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Currency;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

class MonobankResponseToModelMapper {

    private final ISO4217CodeToCurrency iso4217CodeToCurrency;
    private final MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator;

    public MonobankResponseToModelMapper(
            ISO4217CodeToCurrency iso4217CodeToCurrency,
            MonobankRatePriceFromResponseCalculator ratePriceFromResponseCalculator
    ) {
        this.iso4217CodeToCurrency = iso4217CodeToCurrency;
        this.ratePriceFromResponseCalculator = ratePriceFromResponseCalculator;
    }

    List<Rate> mapToRate(List<MonobankRateResponse> monobankRateResponses) {
        return monobankRateResponses.stream()
                .map(this::mapToRate)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Rate mapToRate(MonobankRateResponse monobankRateResponse) {
        Entry<Currency, Currency> currencies = getCurrencies(monobankRateResponse);
        return isBothCurrenciesPresent(currencies)
                ? createRateFromReceivedData(monobankRateResponse, currencies) : null;
    }

    private Rate createRateFromReceivedData(
            MonobankRateResponse monobankRateResponse, Entry<Currency, Currency> currencies
    ) {
        return new Rate(
                currencies.getKey().getCurrencyCode(),
                currencies.getValue().getCurrencyCode(),
                currencies.getKey().getCurrencyCode().concat(currencies.getValue().getCurrencyCode()),
                currencies.getKey().getDefaultFractionDigits(),
                currencies.getValue().getDefaultFractionDigits(),
                ratePriceFromResponseCalculator.getRatePrice(monobankRateResponse),
                false
        );
    }

    private boolean isBothCurrenciesPresent(Entry<Currency, Currency> currenciesEntry) {
        return (currenciesEntry.getKey() != null) && (currenciesEntry.getValue() != null);
    }

    private Entry<Currency, Currency> getCurrencies(MonobankRateResponse monobankRateResponse) {
        return new SimpleImmutableEntry<>(
                getCurrencyTickerByCode(monobankRateResponse.getCurrencyCodeA()),
                getCurrencyTickerByCode(monobankRateResponse.getCurrencyCodeB())
        );
    }

    private Currency getCurrencyTickerByCode(Integer currencyCode) {
        return iso4217CodeToCurrency
                .getCurrencyTickerByCode(currencyCode)
                .orElse(null);
    }

}
