package com.wiremit.forex_aggregator_service.features.rates.controllers;

import com.wiremit.forex_aggregator_service.features.rates.services.AdminService;
import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "authorization")
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    // to include admin-specific endpoints related to currency markups and adding more currencies
    private final AdminService adminService;

    @GetMapping("/currencies")
    public ResponseEntity<GenericResponse> getCurrencies() {
        return adminService.getCurrencies();
    }

    @GetMapping("/users")
    public ResponseEntity<GenericResponse> getUsers() {
        return adminService.getUsers();
    }

    @PostMapping("/currencies")
    public ResponseEntity<GenericResponse> addCurrency(@RequestParam String currencyCode, @RequestParam String currencyName) {
        return adminService.addCurrency(currencyCode, currencyName);
    }

    @GetMapping("/markups")
    public ResponseEntity<GenericResponse> getMarkUps() {
        return adminService.getMarkUps();
    }

    @GetMapping("/markups/active")
    public ResponseEntity<GenericResponse> getActiveMarkup() {
        return adminService.getActiveMarkup();
    }

    @PostMapping("/markups")
    public ResponseEntity<GenericResponse> addMarkup(@RequestParam double markUpPercentage) {
        return adminService.addMarkup(markUpPercentage);
    }

}
