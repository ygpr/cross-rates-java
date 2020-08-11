package io.github.rates.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class Rate implements PairNameIdentifier {

    private final String asset;
    private final String quotable;
    private final String pairName;
    private final Integer assetPrecision;
    private final Integer quotablePrecision;
    private final BigDecimal price;
    private final boolean crypto;

    public Rate(
            String asset, String quotable, String pairName, Integer assetPrecision, Integer quotablePrecision,
            BigDecimal price
    ) {
        this(asset, quotable, pairName, assetPrecision, quotablePrecision, price, false);
    }

}
