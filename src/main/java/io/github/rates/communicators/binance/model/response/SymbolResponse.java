package io.github.rates.communicators.binance.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.rates.domain.PairNameIdentifier;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolResponse implements PairNameIdentifier {

    private final String symbol;
    private final String baseAsset;
    private final String quoteAsset;

    @JsonCreator
    public SymbolResponse(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("baseAsset") String baseAsset,
            @JsonProperty("quoteAsset") String quoteAsset
    ) {
        this.symbol = symbol;
        this.baseAsset = baseAsset;
        this.quoteAsset = quoteAsset;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getBaseAsset() {
        return baseAsset;
    }

    public String getQuoteAsset() {
        return quoteAsset;
    }

    @Override
    public String getPairName() {
        return getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolResponse that = (SymbolResponse) o;
        return Objects.equals(symbol, that.symbol)
                && Objects.equals(baseAsset, that.baseAsset)
                && Objects.equals(quoteAsset, that.quoteAsset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, baseAsset, quoteAsset);
    }

    @Override
    public String toString() {
        return "TradedPairSymbolsResponse{" +
                "symbol='" + symbol + '\'' +
                ", baseAsset='" + baseAsset + '\'' +
                ", quoteAsset='" + quoteAsset + '\'' +
                '}';
    }
}
