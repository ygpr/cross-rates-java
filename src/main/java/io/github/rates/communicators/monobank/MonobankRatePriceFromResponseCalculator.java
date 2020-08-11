package io.github.rates.communicators.monobank;

import io.github.rates.communicators.monobank.model.MonobankRateResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

class MonobankRatePriceFromResponseCalculator {

    BigDecimal getRatePrice(MonobankRateResponse monobankRateResponse) {
        try {
            return monobankRateResponse.getRateCross() != null
                    ? monobankRateResponse.getRateCross()
                    : monobankRateResponse.getRateBuy()
                    .add(monobankRateResponse.getRateSell())
                    .divide(BigDecimal.valueOf(2), RoundingMode.DOWN);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format(
                    "There is null value that doesn't allow to calculate rate price %s",
                    monobankRateResponse
            ));
        }
    }
}
