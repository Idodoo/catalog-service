package com.idev.catalogservice.ui.controllers;


import com.idev.catalogservice.ApplicationProperties;
import com.idev.catalogservice.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
        Product product =  new Product("P002", "Test Product", "This is a test product", "http://example.com/image.jpg", BigDecimal.valueOf(19.9));

        when(productService.getProductByCode("P002")).thenReturn(Optional.of(product));

        ResponseEntity<Product> response = productController.getProductByCode("P002");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
    }

    @Test
    void createProduct_givenFindByCode_returnOfProductEntity(){
        ProductEntity productEntity = new ProductEntity();
        productEntity.setCode("P001");
        productEntity.setId(1L);
        productEntity.setName("Test Product");
        productEntity.setDescription("This is a test product");
        productEntity.setImageUrl("http://example.com/image.jpg");
        productEntity.setPrice(BigDecimal.valueOf(15.9));
        Optional<ProductEntity> ofResult = Optional.of(productEntity);
        ProductRepository productRepository = mock(ProductRepository.class);
        when(productRepository.findByCode(Mockito.<String>any())).thenReturn(ofResult);
        ProductController productController = new ProductController(new ProductService(productRepository, new ApplicationProperties(10)));

        assertThrows(ProductAlreadyExistsException.class,() ->
            productController.createProduct(new ProductDTO("P001", "Test Product", "This is a test product", "http://example.com/image.jpg", BigDecimal.valueOf(15.9))));

        verify(productRepository).findByCode(eq("P001"));

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
}
