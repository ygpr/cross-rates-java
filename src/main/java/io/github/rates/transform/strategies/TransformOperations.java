package io.github.rates.transform.strategies;

import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.tools.math.CurrencyConvertingDecimal;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

class TransformOperations {

    private static volatile TransformOperations transformOperationsInstance;

    static final String BITCOIN_TICKER = "BTC";
    static final String ETHERIUM_TICKER = "ETH";
    static final String USD_TETHER_TICKER = "USDT";
    static final String USD_TICKER = "USD";
    static final String EURO_TICKER = "EUR";
    static final String UKRAINIAN_HRYVNIA_TICKER = "UAH";

    private final RatesSupplier ratesSupplier;

    private TransformOperations(RatesSupplier ratesSupplier) {
        this.ratesSupplier = ratesSupplier;
    }

    static TransformOperations getInstance(RatesSupplier ratesSupplier) {
        if (transformOperationsInstance == null) {
            synchronized (TransformOperations.class) {
                if (transformOperationsInstance == null) {
                    transformOperationsInstance = new TransformOperations(ratesSupplier);
                }
            }
        }
        return transformOperationsInstance;
    }

    Optional<BigDecimal> transformCryptoCurrencies(BigDecimal amount, String currencyFrom, String currencyTo) {
        return getCryptoPriceOrTetherEquivalent(currencyFrom, currencyTo)
                .map(convertDirectly(amount))
                .or(() -> getPriceBySwappingCurrencies(amount, currencyFrom, currencyTo));
    }

    Optional<BigDecimal> getCryptoPriceOrTetherEquivalent(String currencyFrom, String currencyTo) {
        return getRate(currencyFrom, currencyTo)
                .map(Rate::getPrice)
                .or(() -> getUnitPriceOfCurrencyBySwapping(currencyFrom, currencyTo))
                .or(() -> getPriceViaTether(currencyFrom, currencyTo));
    }

    Optional<Rate> getRate(String currencyFrom, String currencyTo) {
        return ratesSupplier.getRate(currencyFrom, currencyTo);
    }

    private Optional<BigDecimal> getPriceBySwappingCurrencies(BigDecimal amount, String currencyFrom, String currencyTo) {
        return getCryptoPriceOrTetherEquivalent(currencyTo, currencyFrom)
                .map(price -> convertUsingCrossCourse(price, amount));
    }

    private Optional<BigDecimal> getUnitPriceOfCurrencyBySwapping(String currencyFrom, String currencyTo) {
        return getRate(currencyTo, currencyFrom)
                .map(rate -> convertUsingCrossCourse(rate.getPrice(), BigDecimal.ONE));
    }

    private Optional<BigDecimal> getPriceViaTether(String currencyFrom, String currencyTo) {
        return getRate(currencyFrom, USD_TETHER_TICKER)
                .flatMap(rateToUSDT -> getRate(USD_TETHER_TICKER, currencyTo)
                        .map(Rate::getPrice)
                        .map(divideTetherResults(rateToUSDT.getPrice())));
    }

    private Function<BigDecimal, BigDecimal> divideTetherResults(BigDecimal requestedCurrencyRateToUSDT) {
        return expectedCurrencyRateToUSDT -> CurrencyConvertingDecimal.from(requestedCurrencyRateToUSDT)
                .divideWithDefaultScaling(expectedCurrencyRateToUSDT);
    }

    private BigDecimal convertUsingCrossCourse(BigDecimal price, BigDecimal amount) {
        return CurrencyConvertingDecimal.from(amount).divideWithDefaultScaling(price);
    }

    private Function<BigDecimal, BigDecimal> convertDirectly(BigDecimal amount) {
        return price -> CurrencyConvertingDecimal.from(amount).multiply(price);
    }

}
