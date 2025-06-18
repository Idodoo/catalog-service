package com.idev.catalogservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Catalog Service APIs")
                        .description("Catalog Service APIs for managing book store products")
                        .version("v1.0.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Ivan Dodoo")
                                .email("dodoo.ivan17@gmail.com")));
    }
}
