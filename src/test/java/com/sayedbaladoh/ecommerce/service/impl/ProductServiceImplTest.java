package com.sayedbaladoh.ecommerce.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.model.Product;
import com.sayedbaladoh.ecommerce.repository.ProductRepository;
import com.sayedbaladoh.ecommerce.util.ObjectMapperHelper;

/**
 * Product service unit tests
 * 
 * Test the Product service logic
 * 
 * @author Sayed Baladoh
 *
 */
@RunWith(SpringRunner.class)
public class ProductServiceImplTest {

	private final Long INVALID_ID = -99L;

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ObjectMapperHelper objectMapperHelper;
	@InjectMocks
	private ProductServiceImpl productService;

	private Product product1;
	private List<Product> mockedProducts;
	private Page<Product> mockedProductsPage;

	@Before
	public void setUp() {
		// Data preparation
		product1 = mockProduct(1l, "Mobile", 500, true);
		Product product2 = mockProduct(2l, "TV", 350, true);
		Product product3 = mockProduct(3l, "Phone", 150, false);

		mockedProducts = List.of(product1, product2, product3);
		mockedProductsPage = new PageImpl<Product>(mockedProducts);

		List<ProductResponseDto> mockedProductsPesponseDto = mockedProducts
				.stream()
				.map(this::mockProductResponseDto)
				.collect(Collectors.toList());
		Page<ProductResponseDto> mockedProductResponseDtoPage = new PageImpl<ProductResponseDto>(
				mockedProductsPesponseDto);

		Mockito.when(productRepository.findById(product1.getId())).thenReturn(Optional.of(product1));
		Mockito.when(productRepository.findById(INVALID_ID).orElse(null)).thenReturn(null);
		Mockito.when(productRepository.findAll(any(Pageable.class))).thenReturn(mockedProductsPage);
		Mockito.when(productRepository.save(any(Product.class))).thenReturn(product1);

		Mockito.when(objectMapperHelper.mapAll(mockedProductsPage, ProductResponseDto.class))
				.thenReturn(mockedProductResponseDtoPage);
		Mockito.when(objectMapperHelper.map(any(ProductRequestDto.class), eq(Product.class))).thenReturn(product1);
		Mockito.when(objectMapperHelper.map(any(Product.class), eq(ProductResponseDto.class)))
				.thenReturn(mockProductResponseDto(product1));
	}
	
	@After
	public void tearDown() {
		Mockito.reset(productRepository);
		Mockito.reset(objectMapperHelper);
	}

	/**
	 * Validate get all products
	 */
	@Test
	public void given3Products_whengetAllProducts_thenReturnProductPageWith3Products() {

		// Method call
		Page<ProductResponseDto> productsPage = productService.getAllProducts(PageRequest.of(0, 5));

		// Verification
		assertThat(productsPage).isNotNull();
		assertThat(productsPage.getContent())
			.hasSize(3)
			.extracting(ProductResponseDto::getName)
			.contains(mockedProducts.get(0).getName(),
					  mockedProducts.get(1).getName(),
					  mockedProducts.get(2).getName());
		assertEquals(productsPage.getNumber(), 0);
		assertEquals(productsPage.getNumberOfElements(), 3);
		assertEquals(productsPage.getTotalElements(), 3);
		assertEquals(productsPage.getTotalPages(), 1);

		Mockito.verify(productRepository, Mockito.times(1)).findAll(PageRequest.of(0, 5));
		Mockito.verifyNoMoreInteractions(productRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).mapAll(mockedProductsPage, ProductResponseDto.class);
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}

	@Test
	public void givenNoProducts_whenGetAllProducts_thenReturnProductPageWithEmptyList() {
		// Data preparation
		PageImpl<Product> mockedProductsPage = new PageImpl<Product>(Collections.emptyList());
		Mockito.when(productRepository.findAll(any(Pageable.class))).thenReturn(mockedProductsPage);
		Mockito.when(objectMapperHelper.mapAll(mockedProductsPage, ProductResponseDto.class))
				.thenReturn(new PageImpl<ProductResponseDto>(Collections.emptyList()));

		// Method call
		Page<ProductResponseDto> productsPage = productService.getAllProducts(PageRequest.of(0, 5));

		// Verification
		assertNotNull(productsPage);
		assertThat(productsPage.getContent()).hasSize(0);
		assertEquals(productsPage.getNumber(), 0);
		assertEquals(productsPage.getNumberOfElements(), 0);
		assertEquals(productsPage.getTotalElements(), 0);
		assertEquals(productsPage.getTotalPages(), 1);

		Mockito.verify(productRepository, Mockito.times(1)).findAll(PageRequest.of(0, 5));
		Mockito.verifyNoMoreInteractions(productRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).mapAll(mockedProductsPage, ProductResponseDto.class);
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}

	/**
	 * Validate get product by Id
	 */
	@Test
	public void givenValidProductId_whenGetProduct_thenReturnProduct() {
		// Method call
		Optional<Product> product = productService.getProduct(product1.getId());

		// Verification
		assertThat(product).isNotNull().isNotEmpty();
		assertEquals(product.get().getId(), product1.getId());
		assertEquals(product.get().getName(), product1.getName());
		assertEquals(product.get().isAvailable(), product1.isAvailable());
		assertThat(product.get().getPrice()).isEqualTo(product1.getPrice());

		Mockito.verify(productRepository, Mockito.times(1)).findById(product1.getId());
		Mockito.verifyNoMoreInteractions(productRepository);
	}

	/**
	 * Validate get product by Id using invalid Id
	 */
	@Test
	public void givenInvalidProductId_whenGetProduct_thenProductShouldNotBeFound() {
		// Method call
		Optional<Product> product = productService.getProduct(INVALID_ID);

		// Verification
		assertThat(product).isNull();

		Mockito.verify(productRepository, Mockito.times(1)).findById(INVALID_ID);
		Mockito.verifyNoMoreInteractions(productRepository);
	}

	/**
	 * Validate add product with valid product
	 */
	@Test
	public void givenValidroduct_whenAddProduct_thenProductShouldBeSavedAndReturned() {
		// Data preparation
		ProductRequestDto productRequest = mockProductRequestDto(product1);

		// Method call
		ProductResponseDto savedProduct = productService.addProduct(productRequest);

		// Verification
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isNotNull();
		assertEquals(savedProduct.getName(), productRequest.getName());
		assertEquals(savedProduct.isAvailable(), productRequest.isAvailable());
		assertThat(savedProduct.getPrice()).isEqualTo(productRequest.getPrice());

		Mockito.verify(productRepository, Mockito.times(1)).save(any(Product.class));
		Mockito.verifyNoMoreInteractions(productRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).map(any(ProductRequestDto.class), eq(Product.class));
		Mockito.verify(objectMapperHelper, Mockito.times(1)).map(any(Product.class), eq(ProductResponseDto.class));
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}

	/**
	 * Validate add product with invalid product
	 */
	@Test
	public void whenInvalidProduct_thenProductShouldNotBeSaved() {
		// Method call
		ProductResponseDto savedProduct = productService.addProduct(null);
		// Verification
		assertThat(savedProduct).isNull();
	}

	/**
	 * Validate update product with valid product
	 */
	@Test
	public void whenValidProduct_thenProductShouldBeUpdatedAndReturned() {

		// Data preparation
		ProductRequestDto productRequest = mockProductRequestDto(product1);
		productRequest.setName("test");

		Product updatedProduct = product1;
		updatedProduct.setName(productRequest.getName());
		Mockito.when(objectMapperHelper.map(productRequest, product1)).thenReturn(updatedProduct);
		Mockito.when(objectMapperHelper.map(any(ProductRequestDto.class), any(Product.class)))
				.thenReturn(updatedProduct);
		Mockito.when(objectMapperHelper.map(any(Product.class), eq(ProductResponseDto.class)))
				.thenReturn(mockProductResponseDto(updatedProduct));

		// Method call
		ProductResponseDto savedProduct = productService.updateProduct(1l, productRequest);

		// Verification
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isNotNull();
		assertEquals(savedProduct.getName(), productRequest.getName());
		assertEquals(savedProduct.isAvailable(), productRequest.isAvailable());
		assertThat(savedProduct.getPrice()).isEqualTo(productRequest.getPrice());

		Mockito.verify(productRepository, Mockito.times(1)).findById(1l);
		Mockito.verify(productRepository, Mockito.times(1)).save(any(Product.class));
		Mockito.verifyNoMoreInteractions(productRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).map(any(ProductRequestDto.class), any(Product.class));
		Mockito.verify(objectMapperHelper, Mockito.times(1)).map(any(Product.class), eq(ProductResponseDto.class));
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}

	private Product mockProduct(Long id, String name, double price, boolean available) {

		return new Product(id, name, price, available, "", "", new Date(), new Date());
	}

	private ProductRequestDto mockProductRequestDto(Product product) {

		ProductRequestDto productRequest = new ProductRequestDto();
		productRequest.setName(product.getName());
		productRequest.setPrice(product.getPrice());
		productRequest.setAvailable(product.isAvailable());
		productRequest.setDescription(product.getDescription());
		productRequest.setImageURL(product.getImageURL());
		return productRequest;
	}

	private ProductResponseDto mockProductResponseDto(Product product) {

		ProductResponseDto productResponse = new ProductResponseDto();
		productResponse.setId(product.getId());
		productResponse.setName(product.getName());
		productResponse.setPrice(product.getPrice());
		productResponse.setAvailable(product.isAvailable());
		productResponse.setDescription(product.getDescription());
		productResponse.setImageURL(product.getImageURL());
		return productResponse;
	}
}
