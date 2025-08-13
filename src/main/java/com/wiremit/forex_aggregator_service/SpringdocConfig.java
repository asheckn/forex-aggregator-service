package com.wiremit.forex_aggregator_service;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
@SecurityScheme(
        type = SecuritySchemeType.APIKEY, name = "authorization", in = SecuritySchemeIn.HEADER)
public class SpringdocConfig {

    @Bean
    public OpenAPI baseOpenApi(){
        return new OpenAPI().info(new Info().title("Wiremit Forex Aggregator").version("0.0.1").description("Forex Aggregator Service"));
    }
}
