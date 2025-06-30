package com.idev.catalogservice.config;

import com.idev.catalogservice.ApplicationProperties;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final ApplicationProperties properties;

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Catalog Service APIs")
                        .description("Catalog Service APIs for managing book store products")
                        .version("v" + properties.version())
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Ivan Dodoo")
                                .email("dodoo.ivan17@gmail.com")));
    }
}
