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
    private final Integer baseAssetPrecision;
    private final Integer quotePrecision;

    @JsonCreator
    public SymbolResponse(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("baseAsset") String baseAsset,
            @JsonProperty("quoteAsset") String quoteAsset,
            @JsonProperty("baseAssetPrecision") Integer baseAssetPrecision,
            @JsonProperty("quotePrecision") Integer quotePrecision
    ) {
        this.symbol = symbol;
        this.baseAsset = baseAsset;
        this.quoteAsset = quoteAsset;
        this.baseAssetPrecision = baseAssetPrecision;
        this.quotePrecision = quotePrecision;
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

    public Integer getBaseAssetPrecision() {
        return baseAssetPrecision;
    }

    public Integer getQuotePrecision() {
        return quotePrecision;
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
        return "SymbolResponse{" +
                "symbol='" + symbol + '\'' +
                ", baseAsset='" + baseAsset + '\'' +
                ", quoteAsset='" + quoteAsset + '\'' +
                ", baseAssetPrecision=" + baseAssetPrecision +
                ", quotePrecision=" + quotePrecision +
                '}';
    }
}
