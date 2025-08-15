package com.wiremit.forex_aggregator_service.features.rates.services;

import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    ResponseEntity<GenericResponse> getCurrencies();

    ResponseEntity<GenericResponse> getUsers();
    ResponseEntity<GenericResponse> addCurrency(String currencyCode, String currencyName);
    ResponseEntity<GenericResponse> getMarkUps();
    ResponseEntity<GenericResponse> getActiveMarkup();

    ResponseEntity<GenericResponse> addMarkup(double markupPercentage);
}
