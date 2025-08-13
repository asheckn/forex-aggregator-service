package com.wiremit.forex_aggregator_service.features.auth;


public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber
) {

}
