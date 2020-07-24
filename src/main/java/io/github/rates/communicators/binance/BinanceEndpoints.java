package io.github.rates.communicators.binance;

class BinanceEndpoints {

    private static final String API_ROOT = "https://api.binance.com/api";
    private static final String API_VERSION = "/v3";
    public final static String RATES = API_ROOT + API_VERSION + "/ticker/price";
    public final static String EXCHANGE_INFO = API_ROOT + API_VERSION + "/exchangeInfo";

}
