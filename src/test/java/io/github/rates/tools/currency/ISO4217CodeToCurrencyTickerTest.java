package io.github.rates.tools.currency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Optional;

class ISO4217CodeToCurrencyTickerTest {

    ISO4217CodeToCurrencyTicker codeToCurrencyTicker = new ISO4217CodeToCurrencyTicker();

    @Test
    void getSymbolByCode() {
        Integer currencyCode = 980;
        String currency = "UAH";

        Optional<String> currencyOpt = codeToCurrencyTicker.getCurrencyTickerByCode(currencyCode);

        assertTrue(currencyOpt.isPresent());
        assertEquals(currency, currencyOpt.get());
    }

    @Test
    void getSymbolByCode_symbolNotFound() {
        Integer currencyCode = 232252;

        assertTrue(codeToCurrencyTicker.getCurrencyTickerByCode(currencyCode).isEmpty());
    }

    @Test
    void getSymbolByCode_symbolInBlackList_shouldReturnEmptyResult() {
        assertTrue(codeToCurrencyTicker.getCurrencyTickerByCode(0).isEmpty());
    }
}
