package com.wiremit.forex_aggregator_service.features.rates.dtos;

import java.time.LocalDate;
import java.util.Map;

public record RateResponse(String base, LocalDate date, Map<String, Double> rates) {

    public RateResponse(String base, LocalDate date, Map<String, Double> rates) {
        this.base = base;
        this.date = date;
        this.rates = rates;
    }
}
