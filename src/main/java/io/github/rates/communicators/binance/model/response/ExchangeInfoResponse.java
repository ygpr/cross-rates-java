package io.github.rates.communicators.binance.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeInfoResponse {

    private final List<SymbolResponse> symbols;

    @JsonCreator
    public ExchangeInfoResponse(@JsonProperty("symbols") List<SymbolResponse> symbols) {
        this.symbols = symbols;
    }

    public List<SymbolResponse> getSymbols() {
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeInfoResponse that = (ExchangeInfoResponse) o;
        return symbols.containsAll(that.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbols);
    }

    @Override
    public String toString() {
        return "ExchangeInfoResponse{" +
                "symbols=" + symbols +
                '}';
    }
}
