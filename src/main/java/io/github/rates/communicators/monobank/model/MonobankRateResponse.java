package io.github.rates.communicators.monobank.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonobankRateResponse {

    private final Integer currencyCodeA;
    private final Integer currencyCodeB;
    private final BigDecimal rateSell;
    private final BigDecimal rateBuy;
    private final BigDecimal rateCross;
    private final Long date;

    @JsonCreator
    public MonobankRateResponse(
            @JsonProperty("currencyCodeA") Integer currencyCodeA,
            @JsonProperty("currencyCodeB") Integer currencyCodeB,
            @JsonProperty("rateSell") BigDecimal rateSell,
            @JsonProperty("rateBuy") BigDecimal rateBuy,
            @JsonProperty("rateCross") BigDecimal rateCross,
            @JsonProperty("date") Long date
    ) {
        this.currencyCodeA = currencyCodeA;
        this.currencyCodeB = currencyCodeB;
        this.date = date;
        this.rateSell = rateSell;
        this.rateBuy = rateBuy;
        this.rateCross = rateCross;
    }

    public Integer getCurrencyCodeA() {
        return currencyCodeA;
    }

    public Integer getCurrencyCodeB() {
        return currencyCodeB;
    }

    public BigDecimal getRateSell() {
        return rateSell;
    }

    public BigDecimal getRateBuy() {
        return rateBuy;
    }

    public BigDecimal getRateCross() {
        return rateCross;
    }

    public Long getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonobankRateResponse that = (MonobankRateResponse) o;
        return Objects.equals(currencyCodeA, that.currencyCodeA)
                && Objects.equals(currencyCodeB, that.currencyCodeB)
                && Objects.equals(rateSell, that.rateSell)
                && Objects.equals(rateBuy, that.rateBuy)
                && Objects.equals(rateCross, that.rateCross)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCodeA, currencyCodeB, rateSell, rateBuy, rateCross, date);
    }

    @Override
    public String toString() {
        return "MonobankRateResponse{" +
                "currencyCodeA=" + currencyCodeA +
                ", currencyCodeB=" + currencyCodeB +
                ", rateSell=" + rateSell +
                ", rateBuy=" + rateBuy +
                ", rateCross=" + rateCross +
                ", date=" + date +
                '}';
    }
}
