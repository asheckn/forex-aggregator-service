package com.wiremit.forex_aggregator_service.features.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private Boolean success;
    private String token;
    private String description;
    private Object data;

}
