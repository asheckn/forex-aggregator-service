package com.wiremit.forex_aggregator_service.features.rates.services;

import com.wiremit.forex_aggregator_service.features.rates.entities.Currency;
import com.wiremit.forex_aggregator_service.features.rates.entities.Markup;
import com.wiremit.forex_aggregator_service.features.rates.repositories.CurrencyRepository;
import com.wiremit.forex_aggregator_service.features.rates.repositories.MarkupRepository;
import com.wiremit.forex_aggregator_service.features.user.User;
import com.wiremit.forex_aggregator_service.features.user.UserRepository;
import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final CurrencyRepository currencyRepository;

    private final MarkupRepository markupRepository;

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<GenericResponse> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return ResponseEntity.ok(new GenericResponse("Success", true, currencies));
    }

    @Override
    public ResponseEntity<GenericResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new GenericResponse("Success", true, users));
    }

    @Override
    public ResponseEntity<GenericResponse> addCurrency(String currencyCode, String currencyName) {

        // check if currency exists
        if (currencyRepository.findByCode(currencyCode).isPresent()) {
            return ResponseEntity.badRequest().body(new GenericResponse("Currency already exists", false, null));
        }

        Currency currency = new Currency();
        currency.setCode(currencyCode);
        currency.setName(currencyName);
        currencyRepository.save(currency);
        return ResponseEntity.ok(new GenericResponse("Currency added successfully", true, currency));
    }

    @Override
    public ResponseEntity<GenericResponse> getMarkUps() {
        List<Markup> markups = markupRepository.findAll();
        return ResponseEntity.ok(new GenericResponse("Success", true, markups));
    }

    @Override
    public ResponseEntity<GenericResponse> getActiveMarkup() {
        Markup activeMarkup = markupRepository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .findFirst()
                .orElse(null);
        if (activeMarkup == null) {
            return ResponseEntity.ok(new GenericResponse("No active markup found", true, null));
        }
        return ResponseEntity.ok(new GenericResponse("Active markup retrieved successfully", true, activeMarkup));
    }

    @Override
    public ResponseEntity<GenericResponse> addMarkup(double markupPercentage) {
        // Validate markup percentage
        Markup markup = new Markup();
        markup.setPercentage(markupPercentage);
        markupRepository.save(markup);
        return ResponseEntity.ok(new GenericResponse("Markup added successfully", true, markup));
    }
}
