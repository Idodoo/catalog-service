package com.idev.catalogservice.ui.controllers;

import com.idev.catalogservice.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base-path}")
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
        Product product = productService.getProductByCode(code);
        return ResponseEntity.ok(product);
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
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping
    @Operation(summary = "Create new product",
            description = "Creates a new product with the provided details.", tags = {"Products"})
    ResponseEntity<Product> createProduct(@Valid @RequestBody ProductDTO product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(201).body(createdProduct);
    }

}
