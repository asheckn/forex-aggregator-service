package com.wiremit.forex_aggregator_service.features.rates;

import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class RatesServiceImpl implements RatesService {
    @Override
    public ResponseEntity<GenericResponse> getRates(String baseCurrency) {
//        Mock Rate response
     List<RateResponse> rateResponse = List.of(new RateResponse("USD", baseCurrency, 1.0));
        return ResponseEntity.ok(new GenericResponse("Success", true, rateResponse));
    }

    @Override
    public ResponseEntity<GenericResponse> getAllRates() {
        return null;
    }

    @Override
    public ResponseEntity<GenericResponse> getRatesByDateCurrency(String baseCurrency, LocalDate date) {
        return null;
    }

    @Override
    public ResponseEntity<GenericResponse> getRatesByDate(LocalDate date) {
        return null;
    }
}
