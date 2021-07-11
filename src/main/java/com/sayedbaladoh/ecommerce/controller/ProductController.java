package com.sayedbaladoh.ecommerce.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sayedbaladoh.ecommerce.dto.common.ApiAuthorization;
import com.sayedbaladoh.ecommerce.dto.common.ApiPageableAuthorization;
import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Products Rest Controller
 * 
 * @author SayedBaladoh
 */
@Api(value = "Products", description = "Product's operations APIs", tags = { "Products" })
@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	@ApiOperation(value = "Return paginated list of products", nickname = "getAllProducts", notes = "Get paginated list of products", tags = {
			"Products" }, response = Page.class)
	@ApiPageableAuthorization
	@GetMapping(produces = { "application/json" })
	public ResponseEntity<Page<ProductResponseDto>> getProducts(@ApiIgnore Pageable pageable) {

		Page<ProductResponseDto> products = productService.getAllProducts(pageable);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	@ApiOperation(value = "Return a product details", nickname = "getProduct", notes = "Get a product details", tags = {
			"Products" }, response = ProductResponseDto.class)
	@ApiAuthorization
	@GetMapping(value = "/{productID}", produces = { "application/json" })
	public ResponseEntity<ProductResponseDto> getProduct(@PathVariable("productID") Long productID) {

		return new ResponseEntity<>(productService.getProductDto(productID), HttpStatus.OK);
	}

	@ApiOperation(value = "Add a new product", nickname = "addProduct", notes = "Insert a new product", tags = {
			"Products" }, response = ProductResponseDto.class)
	@ApiAuthorization
	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<ProductResponseDto> addProduct(@Valid @RequestBody ProductRequestDto productDto) {

		ProductResponseDto product = productService.addProduct(productDto);
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/products/{id}")
				.buildAndExpand(product.getId()).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);

		return new ResponseEntity<>(product, headers, HttpStatus.CREATED);
	}

	@ApiOperation(value = "Edit a product details", nickname = "updateProduct", notes = "Update a product details", tags = {
			"Products" }, response = ProductResponseDto.class)
	@ApiAuthorization
	@PutMapping(value = "/{productID}", consumes = { "application/json" })
	public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable("productID") Long productID,
			@RequestBody @Valid ProductRequestDto productDto) {

		return new ResponseEntity<>(productService.updateProduct(productID, productDto), HttpStatus.OK);
	}

}
