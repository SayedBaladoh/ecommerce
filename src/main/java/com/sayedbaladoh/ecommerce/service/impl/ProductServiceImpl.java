package com.sayedbaladoh.ecommerce.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.exception.ResourceNotFoundException;
import com.sayedbaladoh.ecommerce.model.Product;
import com.sayedbaladoh.ecommerce.repository.ProductRepository;
import com.sayedbaladoh.ecommerce.service.ProductService;
import com.sayedbaladoh.ecommerce.util.ObjectMapperHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ObjectMapperHelper objectMapperHelper;

	@Override
	public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
		return objectMapperHelper.mapAll(productRepository.findAll(pageable), ProductResponseDto.class);
	}

	@Override
	public ProductResponseDto getProductDto(long id) {
		return objectMapperHelper.map(get(id), ProductResponseDto.class);
	}

	@Override
	public Optional<Product> getProduct(long id) {
		return productRepository.findById(id);
	}

	@Override
	public ProductResponseDto addProduct(ProductRequestDto productDto) {
		Product product = objectMapperHelper.map(productDto, Product.class);
		return save(product);
	}

	@Override
	public ProductResponseDto updateProduct(Long id, ProductRequestDto productDto) {
		Product product = objectMapperHelper.map(productDto, get(id));
		product.setId(id);
		return save(product);
	}

	private Product get(long id) {
		return productRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException(String.format("Product with id: %d is not found.", id)));
	}

	private ProductResponseDto save(Product product) {
		return objectMapperHelper.map(productRepository.save(product), ProductResponseDto.class);
	}
}