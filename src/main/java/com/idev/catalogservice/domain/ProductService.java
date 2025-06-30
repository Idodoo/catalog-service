package com.idev.catalogservice.domain;

import com.idev.catalogservice.ApplicationProperties;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ApplicationProperties properties;


    @Transactional(readOnly = true)
    public PagedResult<Product> getProducts(int pageNo) {
        Sort sort = Sort.by("name").ascending();
        pageNo = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);
        Page<Product> productsPage = productRepository.findAll(pageable).map(ProductMapper::toProduct);

        return new PagedResult<>(
                productsPage.getContent(),
                productsPage.getTotalElements(),
                productsPage.getNumber() + 1,
                productsPage.getTotalPages(),
                productsPage.isFirst(),
                productsPage.isLast(),
                productsPage.hasNext(),
                productsPage.hasPrevious());
    }


    @Transactional(readOnly = true)
    public Product getProductByCode(String code) {
        return productRepository.findByCode(code)
                .map(ProductMapper::toProduct)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with code: " + code));
    }


    public Product createProduct(ProductDTO product) {
        if (productRepository.findByCode(product.getCode()).isPresent()) {
            throw new ProductAlreadyExistsException("Product already exists with code: " + product.getCode());
        }
        ProductEntity productEntity = ProductMapper.toProductEntity(product);
        return ProductMapper.toProduct(productRepository.save(productEntity));


    }



    public Product updateProduct(ProductDTO product) {
        return productRepository.findByCode(product.getCode())
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setImageUrl(product.getImageUrl());
                    existingProduct.setPrice(product.getPrice());
                    return ProductMapper.toProduct(productRepository.save(existingProduct));
                })
                .orElseThrow(() -> new ProductNotFoundException("Product not found with code: " + product.getCode()));
    }


    public void deleteProductByCode(String code) {
        productRepository.findByCode(code).ifPresent(productRepository::delete);
    }

}
