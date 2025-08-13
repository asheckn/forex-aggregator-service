package com.wiremit.forex_aggregator_service.features.rates;

public record RateResponse(String targetCurrency,
                           String baseCurrency,
                           double rate,
                           String date) {

    public RateResponse(String targetCurrency, String baseCurrency, double rate) {
        this(targetCurrency, baseCurrency, rate, null);
    }

    public RateResponse(String targetCurrency, String baseCurrency) {
        this(targetCurrency, baseCurrency, 0.0);
    }
}
