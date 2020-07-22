package io.github.rates.parsers;

public interface JsonParser<FROM,TO> {

    TO parseJson(FROM from);
}
