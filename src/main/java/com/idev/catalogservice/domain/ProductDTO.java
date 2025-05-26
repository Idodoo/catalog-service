package com.idev.catalogservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductDTO {
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;


}
