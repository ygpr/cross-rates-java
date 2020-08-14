package io.github.rates.transform.strategies;

import static io.github.rates.transform.strategies.TransformOperations.*;

import io.github.rates.domain.Rate;
import io.github.rates.suppliers.RatesSupplier;
import io.github.rates.tools.currency.ISO4217CodeToCurrency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

class PrecisionNormalizer {

    private static volatile PrecisionNormalizer precisionNormalizerInstance;

    private static final List<String> PIVOTAL_CRYPTO_CURRENCIES = List.of(BITCOIN_TICKER, ETHERIUM_TICKER, USD_TETHER_TICKER);

    private final ISO4217CodeToCurrency iso4217CodeToCurrency;
    private final RatesSupplier ratesSupplier;

    private PrecisionNormalizer(ISO4217CodeToCurrency iso4217CodeToCurrency, RatesSupplier ratesSupplier) {
        this.iso4217CodeToCurrency = iso4217CodeToCurrency;
        this.ratesSupplier = ratesSupplier;
    }

    static PrecisionNormalizer getInstance(RatesSupplier ratesSupplier) {
        if (precisionNormalizerInstance == null) {
            synchronized (PrecisionNormalizer.class) {
                if (precisionNormalizerInstance == null) {
                    precisionNormalizerInstance = new PrecisionNormalizer(ISO4217CodeToCurrency.getInstance(), ratesSupplier);
                }
            }
        }
        return precisionNormalizerInstance;
    }

    BigDecimal normalize(BigDecimal toNormalize, String sourceCurrency) {
        return toNormalize.setScale(16, RoundingMode.HALF_UP);
        // fixme: decide what to do with fiat currencies, 2 digits after decimal point is too little on multiplying
//        return isSourceCurrencyIsFiat(sourceCurrency)
//                ? normalizeFiat(toNormalize, sourceCurrency)
//                : normalizeCrypto(toNormalize, sourceCurrency);
    }

    private BigDecimal normalizeFiat(BigDecimal toNormalize, String sourceCurrency) {
        return getFiatSourceRate(sourceCurrency)
                .map(Rate::getAssetPrecision)
                .map(precision -> toNormalize.setScale(precision, RoundingMode.HALF_UP))
                .orElse(toNormalize);
    }

    private BigDecimal normalizeCrypto(BigDecimal toNormalize, String sourceCurrency) {
        return PIVOTAL_CRYPTO_CURRENCIES.stream()
                .map(ticker -> ratesSupplier.getRate(sourceCurrency, ticker))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get)
                .map(Rate::getAssetPrecision)
                .map(precision -> toNormalize.setScale(precision, RoundingMode.HALF_UP))
                .orElse(toNormalize);
    }

    private Optional<Rate> getFiatSourceRate(String sourceCurrency) {
        return sourceCurrency.equalsIgnoreCase(UKRAINIAN_HRYVNIA_TICKER)
                ? ratesSupplier.getRate(USD_TICKER, UKRAINIAN_HRYVNIA_TICKER)
                : ratesSupplier.getRate(sourceCurrency, UKRAINIAN_HRYVNIA_TICKER);
    }

    private boolean isSourceCurrencyIsFiat(String sourceCurrency) {
        return iso4217CodeToCurrency.isFiat(sourceCurrency);
    }
}
