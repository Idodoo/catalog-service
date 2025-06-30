package com.idev.catalogservice.ui.controllers;


import com.idev.catalogservice.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductControllerTest {

    @Test
    void getProductByCode_returnsProduct_whenFoud() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        Product product = new Product("P002", "Test Product", "This is a test product", "http://api.com/image.jpg", BigDecimal.valueOf(19.9));

        when(productService.getProductByCode("P002")).thenReturn(product);

        ResponseEntity<Product> response = productController.getProductByCode("P002");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
    }

    @Test
    void getProductByCode_throwsException_whenNotFound() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);

        when(productService.getProductByCode("P003"))
                .thenThrow(new ProductNotFoundException("Product not found with code: P003"));

        assertThrows(ProductNotFoundException.class, () -> productController.getProductByCode("P003"));
    }

    @Test
    void createProduct_createsProduct_whenValid() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        ProductDTO productDTO = new ProductDTO("P004", "New Prod", "This is a new product", "http://api.com/image.jpg", BigDecimal.valueOf(25.0));
        Product createdProduct = new Product("P004", "New Prod", "This is a new product", "http://api.com/image.jpg", BigDecimal.valueOf(25.0));


        when(productService.createProduct(productDTO)).thenReturn(createdProduct);

        ResponseEntity<Product> response = productController.createProduct(productDTO);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(createdProduct, response.getBody());
    }

    @Test
    void createProduct_throwsException_whenProductAlreadyExists() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        ProductDTO productDTO = new ProductDTO("P001", "Ex Product", "This is an existing product", "http://api.com/image.jpg", BigDecimal.valueOf(15.9));


        when(productService.createProduct(productDTO))
                .thenThrow(new ProductAlreadyExistsException("Product already exists with code: P001"));

        assertThrows(ProductAlreadyExistsException.class, () -> productController.createProduct(productDTO));
    }

    @Test
    void updateProduct_updatesProduct_whenValid() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        ProductDTO productDTO = new ProductDTO("P005", "UP", "EXCC PR", "http://api.com/image.jpg", BigDecimal.valueOf(30.0));
        Product existingProduct = new Product("P005", "OP", "old ERS", "http://api.com/image.jpg", BigDecimal.valueOf(20.0));
        Product updatedProduct = new Product("P005", "UP", "UPPROD", "http://api.com/image.jpg", BigDecimal.valueOf(30.0));


        when(productService.updateProduct(productDTO)).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.updateProduct(productDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedProduct, response.getBody());
    }

    @Test
    void updateProduct_throwsException_whenNotFound() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);
        ProductDTO productDTO = new ProductDTO("P006", "NonExProd", "Doesn't ext", "http://api.com/image.jpg", BigDecimal.valueOf(40.0));

        when(productService.updateProduct(any(ProductDTO.class)))
                .thenThrow(new ProductNotFoundException("Product not found with code: P006"));

        assertThrows(ProductNotFoundException.class, () -> productController.updateProduct(productDTO));
    }

    @Test
    void deleteProductByCode_deletesProduct_whenFound() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);

        doNothing().when(productService).deleteProductByCode("P002");

        ResponseEntity<Void> response = productController.deleteProductByCode("P002");

        assertEquals(204, response.getStatusCodeValue());
        verify(productService, times(1)).deleteProductByCode("P002");
    }

    @Test
    void deleteProductByCode_throwsException_whenNotFound() {
        ProductService productService = mock(ProductService.class);
        ProductController productController = new ProductController(productService);

        doThrow(ProductNotFoundException.forCode("P007")).when(productService).deleteProductByCode("P007");

        assertThrows(ProductNotFoundException.class, () -> productController.deleteProductByCode("P007"));
    }
}