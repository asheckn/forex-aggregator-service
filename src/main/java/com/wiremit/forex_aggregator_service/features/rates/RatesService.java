package com.wiremit.forex_aggregator_service.features.rates;

import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RatesService {

    ResponseEntity<GenericResponse> getRates(String baseCurrency);

    ResponseEntity<GenericResponse> getAllRates();

    ResponseEntity<GenericResponse> getRatesByDateCurrency(String baseCurrency, LocalDate date);

    ResponseEntity<GenericResponse> getRatesByDate(LocalDate date);
}
