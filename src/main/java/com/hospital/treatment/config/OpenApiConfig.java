package com.hospital.treatment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI hospitalOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Hospital Patient Treatment Status API")
                .version("1.0.0")
                .description("Parse, validate and store patient test statuses (C=COMPLETED, P=PENDING)."));
    }
}
