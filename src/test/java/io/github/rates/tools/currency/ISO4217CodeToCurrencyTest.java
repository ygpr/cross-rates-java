package io.github.rates.tools.currency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Currency;
import java.util.Optional;

class ISO4217CodeToCurrencyTest {

    ISO4217CodeToCurrency codeToCurrency = ISO4217CodeToCurrency.getInstance();

    @Test
    void getSymbolByCode() {
        Integer currencyCode = 980;
        String currency = "UAH";

        Optional<Currency> currencyOpt = codeToCurrency.getCurrencyTickerByCode(currencyCode);

        assertTrue(currencyOpt.isPresent());
        assertEquals(currency, currencyOpt.get().getCurrencyCode());
    }

    @Test
    void getSymbolByCode_symbolNotFound() {
        Integer currencyCode = 232252;

        assertTrue(codeToCurrency.getCurrencyTickerByCode(currencyCode).isEmpty());
    }

    @Test
    void getSymbolByCode_symbolInBlackList_shouldReturnEmptyResult() {
        assertTrue(codeToCurrency.getCurrencyTickerByCode(0).isEmpty());
    }

    @Test
    void isFiat_true() {
        assertTrue(codeToCurrency.isFiat("usd"));
    }

    @Test
    void isFiat_false() {
        assertFalse(codeToCurrency.isFiat("USDD"));
    }
}
