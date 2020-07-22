package io.github.rates.parsers;

import static io.github.rates.parsers.JavascriptEngineRatesJsonParser.PARSER_JS_SCRYPT;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ParsersFactory {

    public static RatesJsonParser createRatesJsonParser() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine engine = engineManager.getEngineByName("javascript");
        try {
            engine.eval(PARSER_JS_SCRYPT);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during creating RatesJsonParser", e);
        }
        return new JavascriptEngineRatesJsonParser((Invocable) engine);
    }
}
