package com.idev.catalogservice.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductDTO {
    @NotBlank(message = "Product code is required")
    @Size(min=4, max=10, message = "Product code must be between 4 and 10 characters")
    private String code;
    @NotBlank(message = "Product name is required")
    @Size(min=2, max=50, message = "Product name must be between 2 and 50 characters")
    private String name;
    private String description;
    private String imageUrl;
    @NotNull(message = "Product price is required") @DecimalMin("0.1")
    private BigDecimal price;


}
