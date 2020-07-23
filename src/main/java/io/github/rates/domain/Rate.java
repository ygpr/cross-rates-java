package io.github.rates.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Rate implements PairNameIdentifier {

    private final String asset;
    private final String quotable;
    private final BigDecimal price;

    public Rate(String asset, String quotable, BigDecimal price) {
        this.asset = asset;
        this.quotable = quotable;
        this.price = price;
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

    @Override
    public String getPairName() {
        return asset.concat(quotable).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate = (Rate) o;
        return Objects.equals(asset, rate.asset) && Objects.equals(quotable, rate.quotable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asset, quotable);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "asset='" + asset + '\'' +
                ", quotable='" + quotable + '\'' +
                ", price=" + price +
                '}';
    }
}
