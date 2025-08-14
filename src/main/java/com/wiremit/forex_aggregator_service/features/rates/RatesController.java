package com.wiremit.forex_aggregator_service.features.rates;

import com.wiremit.forex_aggregator_service.features.auth.AuthenticationService;
import com.wiremit.forex_aggregator_service.features.user.User;
import com.wiremit.forex_aggregator_service.utils.GenericResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@SecurityRequirement(name = "authorization")
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class RatesController {

    private final RatesService ratesService;

    @GetMapping("/{currency}")
    public ResponseEntity<GenericResponse> getRate(@PathVariable String currency) {
        return ratesService.getRates(currency);
    }

    @GetMapping("")
    public ResponseEntity<GenericResponse> getRates() {
//        Assumes base currency is USD as world reserve currency
        return ratesService.getAllRates();
    }

    @GetMapping("/historical")
    public ResponseEntity<GenericResponse> getRates(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ratesService.getRatesByDate(startDate, endDate);
    }

    @GetMapping("/{currency}/historical")
    public ResponseEntity<GenericResponse> getRates(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate , @PathVariable String currency) {
        return ratesService.getRatesByDateCurrency(currency, startDate, endDate);
    }

}
