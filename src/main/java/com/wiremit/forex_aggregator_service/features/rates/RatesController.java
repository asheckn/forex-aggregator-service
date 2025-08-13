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

    @GetMapping("/rates/{currency}")
    public ResponseEntity<GenericResponse> getRate(@PathVariable String currency) {
        return ratesService.getRates(currency);
    }

    @GetMapping("/rates")
    public ResponseEntity<GenericResponse> getRates() {
        return ratesService.getAllRates();
    }

    @GetMapping("/rates/historical")
    public ResponseEntity<GenericResponse> getRates(@RequestParam LocalDate date) {
        return ratesService.getRatesByDate(date);
    }

    @GetMapping("/rates/{currency}/historical")
    public ResponseEntity<GenericResponse> getRates(@RequestParam LocalDate date, @PathVariable String currency) {
        return ratesService.getRatesByDateCurrency(currency, date);
    }

}
