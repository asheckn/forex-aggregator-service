package com.wiremit.forex_aggregator_service.features.rates;

import com.wiremit.forex_aggregator_service.features.rates.dtos.RateHistoricalResponse;
import com.wiremit.forex_aggregator_service.features.rates.dtos.RateResponse;
import com.wiremit.forex_aggregator_service.features.rates.entities.Currency;
import com.wiremit.forex_aggregator_service.features.rates.entities.Markup;
import com.wiremit.forex_aggregator_service.features.rates.entities.Rate;
import com.wiremit.forex_aggregator_service.features.rates.repositories.CurrencyRepository;
import com.wiremit.forex_aggregator_service.features.rates.repositories.MarkupRepository;
import com.wiremit.forex_aggregator_service.features.rates.repositories.RateRepository;
import com.wiremit.forex_aggregator_service.utils.ExternalRateClient;
import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatesServiceImpl implements RatesService {
    private final CurrencyRepository currencyRepo;
    private final MarkupRepository markupRepo;
    private final RateRepository rateRepo;
    private final ExternalRateClient externalRateClient;

    @Override
    public ResponseEntity<GenericResponse> getRates(String baseCurrencyCode) {
        LocalDate today = LocalDate.now();
        Currency baseCurrency = currencyRepo.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        List<Rate> rates = rateRepo.findByDateAndBaseCurrency(today, baseCurrency);

        //Map rates to RateResponse
        RateResponse rateResponse = new RateResponse(
                baseCurrency.getCode(),
                today,
                rates.stream()
                        .collect(Collectors.toMap(
                                rate -> rate.getTargetCurrency().getCode(),
                                Rate::getRate
                        ))
        );

        return ResponseEntity.ok(new GenericResponse("Success", true, rateResponse));
    }

    @Value("${rates.validity.hours:1}") // Default to 1 hour validity
    private int rateValidityHours;

    @Scheduled(fixedRate = 600_0000) // every 100 minutes
    public void updateRates() {
        List<Currency> currencies = currencyRepo.findAll();
        double markup = markupRepo.findAll().stream().findFirst()
                .map(Markup::getPercentage)
                .orElse(10.0); // default to 10% if no markup is set This will need to be remved in production

        // Get all unique currency codes
        List<String> currencyCodes = currencies.stream()
                .map(Currency::getCode)
                .collect(Collectors.toList());

        // Check which currency pairs need updates
        List<CurrencyPair> pairsNeedingUpdate = getPairsNeedingUpdate(currencies);

        if (pairsNeedingUpdate.isEmpty()) {
            log.info("All rates are still valid. No API calls needed.");
            return;
        }

        log.info("Need to update {} currency pairs", pairsNeedingUpdate.size());

        // Extract unique currencies that need updating
        Set<String> currenciesNeedingUpdate = pairsNeedingUpdate.stream()
                .flatMap(pair -> Stream.of(pair.getBaseCurrency(), pair.getTargetCurrency()))
                .collect(Collectors.toSet());

        // Fetch rates from all sources only for currencies that need updating
        Map<String, Map<String, List<Double>>> allSourceRates =
                externalRateClient.getAllRatesFromAllSources(new ArrayList<>(currenciesNeedingUpdate));

        // Process only currency pairs that need updating
        for (CurrencyPair pairToUpdate : pairsNeedingUpdate) {
            String baseCode = pairToUpdate.getBaseCurrency();
            String targetCode = pairToUpdate.getTargetCurrency();

            Currency baseCurrency = currencies.stream()
                    .filter(c -> c.getCode().equals(baseCode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Base currency not found: " + baseCode));

            Currency targetCurrency = currencies.stream()
                    .filter(c -> c.getCode().equals(targetCode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Target currency not found: " + targetCode));

            // Get rates from all sources for this pair
            List<Double> ratesFromSources = getRatesForPair(baseCode, targetCode, allSourceRates);

            if (!ratesFromSources.isEmpty()) {
                // Calculate average rate
                double avgRate = ratesFromSources.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);

                if (avgRate > 0) {
                    double finalRate = avgRate * (1 + markup / 100);

                    Rate rateEntity = Rate.builder()
                            .baseCurrency(baseCurrency)
                            .targetCurrency(targetCurrency)
                            .rate(finalRate)
                            .markup(markup)
                            .originalRate(avgRate)
                            .date(LocalDate.now())
                            .build();
                    rateRepo.save(rateEntity);

                    log.debug("Updated rate {}->{}: {} (avg: {}, sources: {})",
                            baseCode, targetCode, finalRate, avgRate, ratesFromSources.size());
                }
            } else {
                log.warn("No rates found for pair {}->{}", baseCode, targetCode);
            }
        }
    }


    private List<CurrencyPair> getPairsNeedingUpdate(List<Currency> currencies) {
            List<CurrencyPair> pairsNeedingUpdate = new ArrayList<>();
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(rateValidityHours);

            for (Currency baseCurrency : currencies) {
            for (Currency targetCurrency : currencies) {
            if (!baseCurrency.equals(targetCurrency)) {
            // Check if we have a valid rate for this pair
            Optional<Rate> existingRate = rateRepo.findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(
            baseCurrency, targetCurrency);

            boolean needsUpdate = existingRate.isEmpty() ||
            existingRate.get().getDate().atStartOfDay().isBefore(cutoffTime);

            if (needsUpdate) {
            pairsNeedingUpdate.add(new CurrencyPair(baseCurrency.getCode(), targetCurrency.getCode()));
            log.debug("Rate {}->{} needs update. Last update: {}",
            baseCurrency.getCode(), targetCurrency.getCode(),
            existingRate.map(rate -> rate.getDate().toString()).orElse("NEVER"));
            } else {
            log.debug("Rate {}->{} is still valid until {}",
            baseCurrency.getCode(), targetCurrency.getCode(),
            existingRate.get().getDate().atStartOfDay().plusHours(rateValidityHours));
            }
            }
            }
            }

            return pairsNeedingUpdate;
            }

    // Helper class for currency pairs
    private static class CurrencyPair {
        private final String baseCurrency;
        private final String targetCurrency;

        public CurrencyPair(String baseCurrency, String targetCurrency) {
            this.baseCurrency = baseCurrency;
            this.targetCurrency = targetCurrency;
        }

        public String getBaseCurrency() { return baseCurrency; }
        public String getTargetCurrency() { return targetCurrency; }
    }

    private List<Double> getRatesForPair(String base, String target, Map<String, Map<String, List<Double>>> allSourceRates) {
        List<Double> rates = new ArrayList<>();

        // Try direct rate (base -> target)
        if (allSourceRates.containsKey(base) && allSourceRates.get(base).containsKey(target)) {
            rates.addAll(allSourceRates.get(base).get(target));
        }

        // If no direct rates found, try inverse rate (target -> base)
        if (rates.isEmpty() && allSourceRates.containsKey(target) && allSourceRates.get(target).containsKey(base)) {
            List<Double> inverseRates = allSourceRates.get(target).get(base);
            for (Double inverseRate : inverseRates) {
                if (inverseRate != null && inverseRate > 0) {
                    rates.add(1.0 / inverseRate);
                }
            }
        }

        // If still no rates, try cross-rate through common currencies (USD, EUR)
        if (rates.isEmpty()) {
            for (String intermediateBase : Arrays.asList("USD", "EUR")) {
                if (allSourceRates.containsKey(intermediateBase)) {
                    Map<String, List<Double>> intermediateRates = allSourceRates.get(intermediateBase);
                    if (intermediateRates.containsKey(base) && intermediateRates.containsKey(target)) {
                        List<Double> baseRates = intermediateRates.get(base);
                        List<Double> targetRates = intermediateRates.get(target);

                        // Calculate cross rates for each source that has both rates
                        int minSize = Math.min(baseRates.size(), targetRates.size());
                        for (int i = 0; i < minSize; i++) {
                            Double baseRate = baseRates.get(i);
                            Double targetRate = targetRates.get(i);
                            if (baseRate != null && targetRate != null && baseRate > 0) {
                                // Convert: base -> intermediate -> target
                                double crossRate = targetRate / baseRate;
                                rates.add(crossRate);
                            }
                        }

                        if (!rates.isEmpty()) break; // Found rates through this intermediate
                    }
                }
            }
        }

        return rates;
    }

    @Override
    public ResponseEntity<GenericResponse> getAllRates() {
//        Assumes base currency is world reserve currency USD
        LocalDate today = LocalDate.now();
        Currency baseCurrency = currencyRepo.findByCode("USD")
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        List<Rate> rates = rateRepo.findByDateAndBaseCurrency(today, baseCurrency);

        //Map rates to RateResponse
        RateResponse rateResponse = new RateResponse(
                baseCurrency.getCode(),
                today,
                rates.stream()
                        .collect(Collectors.toMap(
                                rate -> rate.getTargetCurrency().getCode(),
                                Rate::getRate
                        ))
        );

        return ResponseEntity.ok(new GenericResponse("Success", true, rateResponse));
    }

    @Override
    public ResponseEntity<GenericResponse> getRatesByDateCurrency(String baseCurrencyCode, LocalDate startDate, LocalDate endDate) {

        Currency baseCurrency = currencyRepo.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        List<Rate> rates = rateRepo.findByDateBetweenAndBaseCurrency(startDate, endDate, baseCurrency);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Map rates to RateResponse
        RateHistoricalResponse rateResponse = new RateHistoricalResponse(
                baseCurrency.getCode(),
                startDate,
                endDate,
                rates.stream()
                        .collect(Collectors.groupingBy(
                                rate -> rate.getDate().format(formatter),
                                Collectors.toMap(
                                        rate -> rate.getTargetCurrency().getCode(),
                                        Rate::getRate
                                )
                        ))
        );

        return ResponseEntity.ok(new GenericResponse("Success", true, rateResponse));
    }

    @Override
    public ResponseEntity<GenericResponse> getRatesByDate(LocalDate startDate, LocalDate endDate) {
        Currency baseCurrency = currencyRepo.findByCode("USD")
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        List<Rate> rates = rateRepo.findByDateBetweenAndBaseCurrency(startDate, endDate, baseCurrency);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Map rates to RateResponse
        RateHistoricalResponse rateResponse = new RateHistoricalResponse(
                baseCurrency.getCode(),
                startDate,
                endDate,
                rates.stream()
                        .collect(Collectors.groupingBy(
                                rate -> rate.getDate().format(formatter),
                                Collectors.toMap(
                                        rate -> rate.getTargetCurrency().getCode(),
                                        Rate::getRate
                                )
                        ))
        );

        return ResponseEntity.ok(new GenericResponse("Success", true, rateResponse));
    }
}
