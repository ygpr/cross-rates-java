package io.github.rates.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Rate {

    private final String pairName;
    private final BigDecimal price;

    public Rate(String pairName, BigDecimal price) {
        this.pairName = pairName;
        this.price = price;
    }

    public String getPairName() {
        return pairName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate = (Rate) o;
        return Objects.equals(pairName, rate.pairName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pairName);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "pair='" + pairName + '\'' +
                ", price=" + price +
                '}';
    }
}
