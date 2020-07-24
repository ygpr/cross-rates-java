package io.github.rates.tools.currency;

import static java.util.function.Predicate.not;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ISO4217CodeToCurrencyTicker {

    private static final List<String> UNNECESSARY_CURRENCY = List.of("XFU", "XFO", "CSD", "YUM", "ROL");

    private final Map<Integer, String> iso4217CodesToCurrencies;

    {
        this.iso4217CodesToCurrencies = Currency.getAvailableCurrencies().stream()
                .filter(not(currency -> UNNECESSARY_CURRENCY.contains(currency.getSymbol())))
                .collect(Collectors.toMap(Currency::getNumericCode, Currency::getCurrencyCode));
    }

    public Optional<String> getCurrencyTickerByCode(Integer code) {
        return Optional.ofNullable(iso4217CodesToCurrencies.get(code));
    }

}
