package com.wiremit.forex_aggregator_service.features.user;

import com.wiremit.forex_aggregator_service.features.auth.AuthenticationRequest;
import com.wiremit.forex_aggregator_service.features.auth.AuthenticationService;
import com.wiremit.forex_aggregator_service.features.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "authorization")
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService service;

    @GetMapping("/self")
    public ResponseEntity<?> getUserByToken(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(user);
    }
}
