package com.sayedbaladoh.ecommerce.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.model.Product;

public interface ProductService {

	Page<ProductResponseDto> getAllProducts(Pageable pageable);

	ProductResponseDto getProductDto(long id);

	Optional<Product> getProduct(long id);

	ProductResponseDto addProduct(ProductRequestDto product);

	ProductResponseDto updateProduct(Long productID, ProductRequestDto productDto);

}
