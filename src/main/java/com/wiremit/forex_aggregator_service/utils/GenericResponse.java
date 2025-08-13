package com.wiremit.forex_aggregator_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {
    private String message;
    private boolean success;
    private Object data;

    public GenericResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public GenericResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

}
