package io.github.rates.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Rate implements PairNameIdentifier {

    private final String asset;
    private final String quotable;
    private final String pairName;
    private final Integer assetPrecision;
    private final Integer quotablePrecision;
    private final BigDecimal price;

    public Rate(String asset, String quotable, String pairName, Integer assetPrecision, Integer quotablePrecision, BigDecimal price) {
        this.price = price;
        this.asset = asset;
        this.quotable = quotable;
        this.pairName = pairName;
        this.assetPrecision = assetPrecision;
        this.quotablePrecision = quotablePrecision;
    }

    public String getAsset() {
        return asset;
    }

    public String getQuotable() {
        return quotable;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getAssetPrecision() {
        return assetPrecision;
    }

    public Integer getQuotablePrecision() {
        return quotablePrecision;
    }

    @Override
    public String getPairName() {
        return pairName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate = (Rate) o;
        return Objects.equals(asset, rate.asset)
                && Objects.equals(quotable, rate.quotable)
                && Objects.equals(pairName, rate.pairName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset, quotable, pairName);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "asset='" + asset + '\'' +
                ", quotable='" + quotable + '\'' +
                ", pairName='" + pairName + '\'' +
                ", assetPrecision=" + assetPrecision +
                ", quotablePrecision=" + quotablePrecision +
                ", price=" + price +
                '}';
    }
}
