package com.wiremit.forex_aggregator_service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalRateClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.freeCurrencyKey}")
    private String freeCurrencyKey;

    @Value("${api.exchangeRatesKey}")
    private String exchangeRatesKey;

    @Value("${rates.api.delay.frankfurter:100}")
    private int frankfurterDelay;

    @Value("${rates.api.delay.freecurrency:200}")
    private int freeCurrencyDelay;

    @Value("${rates.api.delay.exchangerates:150}")
    private int exchangeRatesDelay;

    /**
     * Fetches rates from all 3 sources for averaging
     * Returns Map<BaseCurrency, Map<TargetCurrency, List<RatesFromSources>>>
     */
    public Map<String, Map<String, List<Double>>> getAllRatesFromAllSources(List<String> currencies) {
        Map<String, Map<String, List<Double>>> consolidatedRates = new HashMap<>();

        // Initialize structure
        for (String base : currencies) {
            consolidatedRates.put(base, new HashMap<>());
            for (String target : currencies) {
                if (!base.equals(target)) {
                    consolidatedRates.get(base).put(target, new ArrayList<>());
                }
            }
        }

        // Fetch from all sources
        List<Map<String, Map<String, Double>>> allSources = new ArrayList<>();

        // Source 1: Frankfurter
        try {
            Map<String, Map<String, Double>> frankfurterRates = fetchAllFromFrankfurter(currencies);
            allSources.add(frankfurterRates);
            log.info("Fetched rates from Frankfurter for {} base currencies", frankfurterRates.size());
        } catch (Exception e) {
            log.error("Frankfurter API failed: {}", e.getMessage());
            allSources.add(new HashMap<>());
        }

        // Source 2: FreeCurrencyAPI
        try {
            Map<String, Map<String, Double>> freeCurrencyRates = fetchAllFromFreeCurrencyApi(currencies);
            allSources.add(freeCurrencyRates);
            log.info("Fetched rates from FreeCurrencyAPI for {} base currencies", freeCurrencyRates.size());
        } catch (Exception e) {
            log.error("FreeCurrencyAPI failed: {}", e.getMessage());
            allSources.add(new HashMap<>());
        }

        // Source 3: Exchange Rates API (or Open Exchange Rates)
        try {
            Map<String, Map<String, Double>> exchangeRatesData = fetchAllFromExchangeRatesApi(currencies);
            allSources.add(exchangeRatesData);
            log.info("Fetched rates from ExchangeRatesAPI for {} base currencies", exchangeRatesData.size());
        } catch (Exception e) {
            log.error("ExchangeRatesAPI failed: {}", e.getMessage());
            allSources.add(new HashMap<>());
        }

        // Combine all sources into the consolidated structure
        for (String base : currencies) {
            for (String target : currencies) {
                if (!base.equals(target)) {
                    List<Double> ratesFromSources = consolidatedRates.get(base).get(target);

                    // Collect rate from each source
                    for (Map<String, Map<String, Double>> sourceRates : allSources) {
                        if (sourceRates.containsKey(base) && sourceRates.get(base).containsKey(target)) {
                            Double rate = sourceRates.get(base).get(target);
                            if (rate != null && rate > 0) {
                                ratesFromSources.add(rate);
                            }
                        }
                    }
                }
            }
        }

        return consolidatedRates;
    }

    private Map<String, Map<String, Double>> fetchAllFromFrankfurter(List<String> currencies) {
        Map<String, Map<String, Double>> allRates = new HashMap<>();

        for (String base : currencies) {
            try {
                List<String> targets = currencies.stream()
                        .filter(c -> !c.equals(base))
                        .collect(Collectors.toList());

                if (!targets.isEmpty()) {
                    String targetsParam = String.join(",", targets);
                    String url = String.format("https://api.frankfurter.dev/v1/latest?base=%s&symbols=%s",
                            base, targetsParam);

                    log.debug("Calling Frankfurter API: {}", url);
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                    if (response != null && response.containsKey("rates")) {
                        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                        allRates.put(base, new HashMap<>(rates));
                        log.debug("Frankfurter returned {} rates for base {}", rates.size(), base);
                    }

                    // Respect rate limits
                    Thread.sleep(frankfurterDelay);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch from Frankfurter for base {}: {}", base, e.getMessage());
            }
        }

        return allRates;
    }

    private Map<String, Map<String, Double>> fetchAllFromFreeCurrencyApi(List<String> currencies) {
        Map<String, Map<String, Double>> allRates = new HashMap<>();

        for (String base : currencies) {
            try {
                List<String> targets = currencies.stream()
                        .filter(c -> !c.equals(base))
                        .collect(Collectors.toList());

                if (!targets.isEmpty()) {
                    String targetsParam = String.join(",", targets);
                    String url = String.format("https://api.freecurrencyapi.com/v1/latest?apikey=%s&base_currency=%s&currencies=%s",
                            freeCurrencyKey, base, targetsParam);

                    log.debug("Calling FreeCurrencyAPI: {}", url.replaceAll("apikey=[^&]*", "apikey=***"));
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                    if (response != null && response.containsKey("data")) {
                        Map<String, Double> rates = (Map<String, Double>) response.get("data");
                        allRates.put(base, new HashMap<>(rates));
                        log.debug("FreeCurrencyAPI returned {} rates for base {}", rates.size(), base);
                    }

                    // Respect rate limits
                    Thread.sleep(freeCurrencyDelay);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch from FreeCurrencyAPI for base {}: {}", base, e.getMessage());
            }
        }

        return allRates;
    }

    private Map<String, Map<String, Double>> fetchAllFromExchangeRatesApi(List<String> currencies) {
        Map<String, Map<String, Double>> allRates = new HashMap<>();

        for (String base : currencies) {
            try {
                List<String> targets = currencies.stream()
                        .filter(c -> !c.equals(base))
                        .collect(Collectors.toList());

                if (!targets.isEmpty()) {
                    String targetsParam = String.join(",", targets);
                    String url;

                    // Try with API key first if available
                    if (exchangeRatesKey != null && !exchangeRatesKey.isEmpty()) {
                        url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s",
                                exchangeRatesKey, base);
                    } else {
                        // Fall back to free tier
                        url = String.format("https://open.er-api.com/v6/latest/%s", base);
                    }

                    log.debug("Calling ExchangeRatesAPI: {}", url.replaceAll("/v6/[^/]*/", "/v6/***/"));
                    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                    if (response != null && response.containsKey("conversion_rates")) {
                        Map<String, Double> allApiRates = (Map<String, Double>) response.get("conversion_rates");

                        // Filter to only include target currencies
                        Map<String, Double> filteredRates = new HashMap<>();
                        for (String target : targets) {
                            if (allApiRates.containsKey(target)) {
                                filteredRates.put(target, allApiRates.get(target));
                            }
                        }

                        if (!filteredRates.isEmpty()) {
                            allRates.put(base, filteredRates);
                            log.debug("ExchangeRatesAPI returned {} rates for base {}", filteredRates.size(), base);
                        }
                    }

                    // Respect rate limits
                    Thread.sleep(exchangeRatesDelay);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch from ExchangeRatesAPI for base {}: {}", base, e.getMessage());
            }
        }

        return allRates;
    }

    /**
     * Legacy method - now properly averages rates from multiple sources
     */
    public double getAverageRate(String base, String target) {
        List<String> currencies = Arrays.asList(base, target);
        Map<String, Map<String, List<Double>>> allRates = getAllRatesFromAllSources(currencies);

        if (allRates.containsKey(base) && allRates.get(base).containsKey(target)) {
            List<Double> rates = allRates.get(base).get(target);
            if (!rates.isEmpty()) {
                return rates.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            }
        }

        return 0.0;
    }
}

