/**
 * 
 */
package com.sayedbaladoh.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.exception.ResourceNotFoundException;
import com.sayedbaladoh.ecommerce.model.Product;
import com.sayedbaladoh.ecommerce.service.ProductService;
import com.sayedbaladoh.ecommerce.util.JsonUtil;


/**
 * Product controller unit tests
 * 
 * Test the Product rest APIs unit tests
 * 
 * @author Sayed Baladoh
 */
@RunWith(SpringRunner.class)
@WebMvcTest(
		value = ProductController.class,
		secure = false)
@EnableSpringDataWebSupport
public class ProductControllerTest {

	@Autowired
	private MockMvc mvc;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@MockBean
	private ProductService productService;
	

	@Before
	public void setUp() {
		reset(productService);
	}

	/**
	 * Validate get all products with list of products
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProducts(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenProductsList_whenGetAllProducts_thenReturnProductPage() throws Exception {
		// Data preparation
		Product product1 = mockProduct(1l, "Mobile", 500, true);
		Product product2 = mockProduct(2l, "TV", 350, true);
		Product product3 = mockProduct(3l, "Phone", 150, false);

		List<Product> products = List.of(product1, product2, product3);	
		List<ProductResponseDto> mockedProductsPesponseDto = products
				.stream()
				.map(this::mockProductResponseDto)
				.collect(Collectors.toList());
		PageImpl<ProductResponseDto> ProductResponseDtoPage = new PageImpl<ProductResponseDto>(
				mockedProductsPesponseDto);
		
		given(productService.getAllProducts(any(Pageable.class)))
				.willReturn(ProductResponseDtoPage);

		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/products?page=0&size=15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.content[0].name", is(product1.getName())))
				.andExpect(jsonPath("$.content[1].name", is(product2.getName())))
				.andExpect(jsonPath("$.content[2].name", is(product3.getName())))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(3)))
				.andExpect(jsonPath("$.totalElements", is(3)))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(productService, times(1)).getAllProducts(any(Pageable.class));
		Mockito.verifyNoMoreInteractions(productService);
	}
	
	/**
	 * Validate get all products with empty list
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProducts(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenEmptyProductsList_whenGetAllProducts_thenReturnProductPageWithEmptyList() throws Exception {
		// Data preparation
		List<Product> products = Collections.emptyList();	
		List<ProductResponseDto> mockedProductsPesponseDto = products
				.stream()
				.map(this::mockProductResponseDto)
				.collect(Collectors.toList());
		PageImpl<ProductResponseDto> ProductResponseDtoPage = new PageImpl<ProductResponseDto>(
				mockedProductsPesponseDto);
		
		given(productService.getAllProducts(any(Pageable.class)))
				.willReturn(ProductResponseDtoPage);

		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/products?page=0&size=15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(0)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(0)))
				.andExpect(jsonPath("$.totalElements", is(0)))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(productService, times(1)).getAllProducts(any(Pageable.class));
		Mockito.verifyNoMoreInteractions(productService);
	}
	
	/**
	 * Verify get product with valid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProduct(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenProduct_whenGetProduct_thenReturnProductResponse() throws Exception {
		// Data preparation
		Product product =  mockProduct(1l, "Mobile", 500, true);

		given(productService.getProductDto(product.getId()))
				.willReturn(mockProductResponseDto(product));

		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/products/{productId}" , product.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.price").exists())
				.andExpect(jsonPath("$.id").value(product.getId()))
				.andExpect(jsonPath("$.name").value(product.getName()))
				.andExpect(jsonPath("$.price").value(product.getPrice()))
				.andDo(print())
				.andReturn();

		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		ProductResponseDto productResponseDto =
		            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductResponseDto.class);
		 
		 assertProduct(productResponseDto, product);
		
		verify(productService, times(1)).getProductDto(product.getId());
		Mockito.verifyNoMoreInteractions(productService);
	}

	/**
	 * Verify get product with invalid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProduct(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenProduct_whenGetProductWithInavlidProductId_thenReturn404NotFound() throws Exception {
		// Data preparation
		long invalidProductId = 55l;

		given(productService.getProductDto(invalidProductId))
				.willThrow(new ResourceNotFoundException());

		// Verification
		this.mvc.perform(get("/products/{productId}", invalidProductId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(print());
		verify(productService, times(1)).getProductDto(invalidProductId);
		Mockito.verifyNoMoreInteractions(productService);
	}

	/**
	 * Verify add a valid Product
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#addProduct(com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidProductRequest_whenAddProduct_thenProductCreated() throws Exception {
		// Data preparation
		Product product =  mockProduct(1l, "Mobile", 500, true);
		ProductRequestDto productRequest =  mockProductRequestDto("Mobile", 500, true);

		given(productService.addProduct(any(ProductRequestDto.class)))
				.willReturn(mockProductResponseDto(product));

		//API call and Verification
		mvc.perform(post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.price").exists())
				.andExpect(jsonPath("$.id").value(product.getId()))
				.andExpect(jsonPath("$.name", is(productRequest.getName())));

		verify(productService, times(1)).addProduct(any(ProductRequestDto.class));
		Mockito.verifyNoMoreInteractions(productService);
	}

	/**
	 * Verify update valid product
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#updateProduct(java.lang.Long, com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidProductRequest_whenUpdateProduct_thenProductUpdated() throws Exception {
		// Data preparation
		Product product =  mockProduct(1l, "Mobile", 500, true);
		ProductRequestDto productRequest =  mockProductRequestDto("Test Mobile", 500, true);
		product.setName(productRequest.getName());

		given(productService.updateProduct(eq(product.getId()), any(ProductRequestDto.class)))
				.willReturn(mockProductResponseDto(product));

		//API call and Verification
		mvc.perform(put("/products/{productId}", product.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequest)))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.price").exists())
				.andExpect(jsonPath("$.id").value(product.getId()))
				.andExpect(jsonPath("$.name").value(product.getName()))
				.andExpect(jsonPath("$.price").value(product.getPrice()))
				.andDo(print());
		
		verify(productService, times(1)).updateProduct(eq(product.getId()), any(ProductRequestDto.class));
		Mockito.verifyNoMoreInteractions(productService);
	}

	/**
	 * Verify update product with invalid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#updateProduct(java.lang.Long, com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenInvalidProductId_whenUpdateProduct_thenReturn404NotFound() throws Exception {
		// Data preparation
		long invalidProductId = 55l;
		ProductRequestDto productRequest =  mockProductRequestDto("Test Mobile", 500, true);
		
		given(productService.updateProduct(eq(invalidProductId), any(ProductRequestDto.class)))
		.willThrow(new ResourceNotFoundException());
		
		//API call and Verification
		mvc.perform(put("/products/{productId}", invalidProductId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequest)))
				.andExpect(status().isNotFound())
				.andDo(print());
		
		verify(productService, times(1)).updateProduct(eq(invalidProductId), any(ProductRequestDto.class));
		Mockito.verifyNoMoreInteractions(productService);
	}
	
	private Product mockProduct(Long id, String name, double price, boolean available) {

		return new Product(id, name, price, available, "", "", new Date(), new Date());
	}
	
	private ProductRequestDto mockProductRequestDto(String name, double price, boolean available) {

		return new ProductRequestDto(name, price, available, "", "");
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
	
	private void assertProduct(ProductResponseDto productDto, Product product) {
		assertNotNull(productDto);
		assertNotNull(productDto.getId());
		assertEquals(productDto.getId(), product.getId());
		assertNotNull(productDto.getName());
	    assertEquals(productDto.getName(), product.getName());
	    assertNotNull(productDto.getPrice());
	    assertThat(productDto.getPrice()).isEqualTo(product.getPrice());
	    assertEquals(productDto.getDescription(), product.getDescription());
	    assertEquals(productDto.getImageURL(), product.getImageURL());
	  }
	
}
