package io.github.rates.domain;

import java.util.Objects;

public class CurrencyComprassionResult {

    private final boolean resultByFromCurrency;
    private final boolean resultByToCurrency;

    public CurrencyComprassionResult(boolean resultByFromCurrency, boolean resultByToCurrency) {
        this.resultByFromCurrency = resultByFromCurrency;
        this.resultByToCurrency = resultByToCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyComprassionResult det = (CurrencyComprassionResult) o;
        return resultByFromCurrency == det.resultByFromCurrency
                && resultByToCurrency == det.resultByToCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultByFromCurrency, resultByToCurrency);
    }
}
