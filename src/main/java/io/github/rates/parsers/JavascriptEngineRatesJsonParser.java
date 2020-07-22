package io.github.rates.parsers;

import static java.util.stream.Collectors.toList;

import io.github.rates.model.Rate;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class JavascriptEngineRatesJsonParser implements RatesJsonParser {

    static final String PARSER_JS_SCRYPT = "var parseFunction = function(body){ return JSON.parse(body)}; ";
    private final Invocable invocable;

    JavascriptEngineRatesJsonParser(Invocable invocable) {
        this.invocable = invocable;
    }

    @Override
    public List<Rate> parseJson(String jsonBody) {
        try {
            return invokeJsFunction(jsonBody).values().stream()
                    .map(mapToRate())
                    .collect(toList());
        } catch (Exception e) {
            throw new RuntimeException("Parse error exception", e);
        }
    }

    private Function<Map<String, String>, Rate> mapToRate() {
        return value -> new Rate(value.get("symbol").toUpperCase(), new BigDecimal(value.get("price")));
    }

    private Map<String, Map<String, String>> invokeJsFunction(String json) throws ScriptException, NoSuchMethodException {
        return (Map<String, Map<String, String>>) invocable.invokeFunction("parseFunction", json);
    }
}
