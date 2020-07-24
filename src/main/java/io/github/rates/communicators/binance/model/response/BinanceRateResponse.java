package io.github.rates.communicators.binance.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rates.domain.PairNameIdentifier;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceRateResponse implements PairNameIdentifier {

    private final String symbol;
    private final BigDecimal price;

    @JsonCreator
    public BinanceRateResponse(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("price") BigDecimal price
    ) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String getPairName() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinanceRateResponse that = (BinanceRateResponse) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price);
    }

    @Override
    public String toString() {
        return "RateResponse{" +
                "symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }
}
