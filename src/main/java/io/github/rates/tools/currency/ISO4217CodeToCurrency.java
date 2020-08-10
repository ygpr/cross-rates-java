package io.github.rates.tools.currency;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableMap;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ISO4217CodeToCurrency {

    private static final List<String> UNNECESSARY_CURRENCY = List.of("XFU", "XFO", "CSD", "YUM", "ROL");
    private static volatile ISO4217CodeToCurrency iSO4217CodeToCurrencyInstance;

    private final Map<Integer, Currency> iso4217CodesToCurrencies;

    private ISO4217CodeToCurrency(Map<Integer, Currency> iso4217CodesToCurrencies) {
        this.iso4217CodesToCurrencies = iso4217CodesToCurrencies;
    }

    public static ISO4217CodeToCurrency getInstance() {
        if (iSO4217CodeToCurrencyInstance == null) {
            synchronized (ISO4217CodeToCurrency.class) {
                if (iSO4217CodeToCurrencyInstance == null) {
                    iSO4217CodeToCurrencyInstance = new ISO4217CodeToCurrency(
                            Currency.getAvailableCurrencies().stream()
                                    .filter(not(currency -> UNNECESSARY_CURRENCY.contains(currency.getSymbol())))
                                    .collect(toUnmodifiableMap(Currency::getNumericCode, Function.identity()))
                    );
                }
            }
        }
        return iSO4217CodeToCurrencyInstance;
    }

    public Optional<Currency> getCurrencyTickerByCode(Integer code) {
        return Optional.ofNullable(iso4217CodesToCurrencies.get(code));
    }

    public boolean isFiat(String currency) {
        try {
            return Currency.getInstance(currency.toUpperCase()) != null;
        } catch (Exception e) {
            return false;
        }
    }

}
