package com.wiremit.forex_aggregator_service.features.user;

public record CreateUserRequest(String firstName, String lastName, String email, String phoneNumber, String address, String password) {
}
