package io.github.rates.communicators.binance;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableMap;

import io.github.rates.communicators.binance.model.response.RateResponse;
import io.github.rates.communicators.binance.model.response.SymbolResponse;
import io.github.rates.domain.PairNameIdentifier;
import io.github.rates.domain.Rate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

class BinanceResponsesToModelMapper {

    List<Rate> mapToRate(List<RateResponse> rateResponses, List<SymbolResponse> symbolsResponses) {
        Map<String, SymbolResponse> symbolsByPairNameMap = getMapWithKeysByPairName(symbolsResponses);
        return rateResponses.stream()
                .map(extractSymbolAndMapItToRate(symbolsByPairNameMap))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Function<RateResponse, Rate> extractSymbolAndMapItToRate(Map<String, SymbolResponse> symbolsByPairNameMap) {
        return rateResponse -> mapToRate(rateResponse, symbolsByPairNameMap.get(rateResponse.getPairName()));
    }

    private Rate mapToRate(RateResponse rateResponse, SymbolResponse symbolResponse) {
        return symbolResponse == null
                ? null
                : new Rate(
                symbolResponse.getBaseAsset(),
                symbolResponse.getQuoteAsset(),
                rateResponse.getPrice());
    }

    private <T extends PairNameIdentifier> Map<String, T> getMapWithKeysByPairName(List<T> responses) {
        return responses.stream()
                .collect(toUnmodifiableMap(PairNameIdentifier::getPairName, Function.identity()));
    }

}
