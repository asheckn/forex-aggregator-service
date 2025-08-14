package com.wiremit.forex_aggregator_service.features.rates.dtos;

import java.time.LocalDate;
import java.util.Map;

public record RateHistoricalResponse(String base, LocalDate startDate,LocalDate endDate,  Map<String, Map<String, Double>> rates) {
}
