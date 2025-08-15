package com.wiremit.forex_aggregator_service.utils;

public class CurrencyPair {
    private final String baseCurrency;
    private final String targetCurrency;

    public CurrencyPair(String baseCurrency, String targetCurrency) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }
}
