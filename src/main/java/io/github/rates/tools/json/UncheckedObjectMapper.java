package io.github.rates.tools.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionException;

public class UncheckedObjectMapper extends ObjectMapper {

    public <T> List<T> readValueAsList(String json, Class<T> responseClass) {
        try {
            return this.readValue(json, this.getTypeFactory().constructCollectionType(List.class, responseClass));
        } catch (IOException e) {
            throw new CompletionException(String.format("Exception occurred during parsing json to List of %s class", responseClass), e);
        }
    }

    public <T> T readValue(String json, Class<T> responseClass) {
        try {
            return this.readValue(json, this.getTypeFactory().constructType(responseClass));
        } catch (IOException e) {
            throw new CompletionException(String.format("Exception occurred during parsing json to %s class", responseClass), e);
        }
    }

}
