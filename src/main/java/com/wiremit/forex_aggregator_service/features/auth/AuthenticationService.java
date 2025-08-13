package com.wiremit.forex_aggregator_service.features.auth;

import org.springframework.http.ResponseEntity;

public interface AuthenticationService{
    ResponseEntity<?> register(RegisterRequest request);
    
    AuthenticationResponse authenticate(AuthenticationRequest request);

}
