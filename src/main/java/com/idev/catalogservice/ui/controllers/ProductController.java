package com.idev.catalogservice.ui.controllers;

import com.idev.catalogservice.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${spring.data.rest.base-path}")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get paginated list of products",
            description = "Returns a paginated list of products with details like name, code, and price.", tags = {"Products"})
    PagedResult<Product> getProducts(@RequestParam(name = "page", defaultValue = "1") int pageNo) {
        return productService.getProducts(pageNo);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get product by code",
            description = "Returns product details for the specified product code.", tags = {"Products"})
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode(code));
    }
    @DeleteMapping("/{code}")
    @Operation(summary = "Delete product by code",
            description = "Deletes the product with the specified code.", tags = {"Products"})
    ResponseEntity<Void> deleteProductByCode(@PathVariable String code) {
        productService.deleteProductByCode(code);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Update product",
            description = "Updates the details of an existing product.", tags = {"Products"})
    ResponseEntity<Product> updateProduct(@Valid @RequestBody ProductDTO product) {
        return productService.getProductByCode(product.getCode())
                .map(existingProduct -> {
                    Product updatedProduct = productService.updateProduct(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseThrow(() -> ProductNotFoundException.forCode(product.getCode()));
    }

    @PostMapping
    @Operation(summary = "Create new product",
            description = "Creates a new product with the provided details.", tags = {"Products"})
    ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO product) {
        if (productService.getProductByCode(product.getCode()).isPresent()) {
            throw new ProductAlreadyExistsException("Product already exists with code: " + product.getCode());
        }
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(201).body(createdProduct);
    }

}
