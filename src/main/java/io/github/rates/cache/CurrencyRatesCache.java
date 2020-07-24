package io.github.rates.cache;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

import io.github.rates.domain.Rate;

import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class CurrencyRatesCache {

    private final NavigableMap<String, Rate> cryptoCurrenciesRates = new ConcurrentSkipListMap<>(CASE_INSENSITIVE_ORDER);

    public Rate getCryptoCurrencyRate(String pairName) {
        return cryptoCurrenciesRates.get(pairName);
    }

    public void updateCryptoCurrenciesRates(List<Rate> cryptoCurrenciesRatesToAdd) {
        cryptoCurrenciesRatesToAdd.forEach(toAdd -> cryptoCurrenciesRates.put(toAdd.getPairName(), toAdd));
    }

    public void clear() {
        cryptoCurrenciesRates.clear();
    }

}
