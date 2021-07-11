package com.sayedbaladoh.ecommerce.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayedbaladoh.ecommerce.EcommerceApplication;
import com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.model.Product;
import com.sayedbaladoh.ecommerce.repository.ProductRepository;
import com.sayedbaladoh.ecommerce.util.JsonUtil;

/**
 * Product APIs Integration tests
 * 
 * Test the Product rest web services' integration tests
 * 
 * @author Sayed Baladoh
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = EcommerceApplication.class)
@AutoConfigureMockMvc
public class ProductRestIntegrationTest {

	private final String API_URL = "/products";
	private final Long INVALID_ID = -99L;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProductRepository productRepository;

	@After
	public void cleanUp() {
		productRepository.deleteAll();
	}

	/**
	 * Validate get all products with list of products
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProducts(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenProducts_whenGetProducts_thenReturnProductsWithStatus200()
			throws Exception {
		// Data preparation
		Product product1 = createProduct("Mobile", 500, true);
		Product product2 = createProduct("TV", 150, true);

		// API call and Verification
		MvcResult mvcResult = mvc.perform(get(API_URL+"?page=0&size=15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(2)))
				.andExpect(jsonPath("$.totalElements", is(2)))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.content", hasSize(equalTo(2))))
				.andExpect(jsonPath("$.content[0].name", is(product1.getName())))
				.andExpect(jsonPath("$.content[1].name", is(product2.getName())))
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}
	
	/**
	 * Validate get all products with empty list
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProducts(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenEmptyProductsList_whenGetAllProducts_thenReturnProductPageWithEmptyList()
			throws Exception {
		
		//API call and Verification
				MvcResult mvcResult = mvc.perform(get("/products?page=0&size=15")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(content()
								.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(jsonPath("$.content", hasSize(0)))
						.andExpect(jsonPath("$.number", is(0)))
						.andExpect(jsonPath("$.numberOfElements", is(0)))
						.andExpect(jsonPath("$.totalElements", is(0)))
						.andExpect(jsonPath("$.totalPages", is(0)))
						.andReturn();
				
				assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}

	/**
	 * Validate get product with valid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProduct(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenProduct_whenGetProductById_thenReturnProductResponse() throws Exception {
		// Data preparation
		Product product = createProduct("Mobile", 500, true);
		
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
	}

	/**
	 * Validate get product with invalid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#getProduct(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenProductAndInavlidProductId_whenGetProductById_thenReturn404NotFound() throws Exception {
		
		// API call and Verification
		mvc.perform(get(API_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound());
	}

	/**
	 * Verify add a valid Product  with authorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#addProduct(com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenValidProductRequest_whenAddProduct_thenProductCreated() throws IOException, Exception {
		// Data preparation		
		ProductRequestDto productRequestDto =  mockProductRequestDto("Mobile", 500, true);

		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL)				
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequestDto)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.price").exists())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name", is(productRequestDto.getName())))
				.andExpect(jsonPath("$.price", is(productRequestDto.getPrice())))
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		ProductResponseDto productResponseDto =
		            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductResponseDto.class);
		 
		 assertProduct(productResponseDto, productRequestDto);

		List<Product> found = productRepository.findAll();
		assertThat(found)
				.extracting(Product::getName)
				.contains(productRequestDto.getName());	
	}

	/**
	 * Verify add an invalid product with authorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#addProduct(com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenInvalidProductRequest_whenAddProduct_thenProductNotCreated() throws IOException, Exception {
		// Data preparation
		ProductRequestDto productRequestDto =  mockProductRequestDto(null, 500, true);

		// API call and Verification
		mvc.perform(post(API_URL)				
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequestDto)))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.id").doesNotExist());
	}

	/**
	 * Verify add a valid Product  with unauthorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#addProduct(com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// @Ignore
	public void givenValidProductRequestAndNotAuthorizedUser_whenAddProduct_thenProductNotCreatedAndReturnUnauthorized() throws IOException, Exception {
		// Data preparation
		ProductRequestDto productRequestDto =  mockProductRequestDto("Mobile", 500, true);

		// Method call and Verification
		mvc.perform(post(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(productRequestDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.id").doesNotExist());
	}

	/**
	 * Verify update valid product with authorized user
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.ProductController#updateProduct(java.lang.Long, com.sayedbaladoh.ecommerce.dto.product.ProductRequestDto)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenValidProductRequest_whenUpdateProduct_thenProductUpdated() throws IOException, Exception {
		// Data preparation
		Product product = createProduct("Mobile", 500, true);

		product.setName("ABC Mobile");
		product.setPrice(350);

		// Method call and Verification
		mvc.perform(put(API_URL + "/" + product.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(product)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists())
				.andExpect(jsonPath("$.price").exists())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.id").value(product.getId()))
				.andExpect(jsonPath("$.name", is(product.getName())))
				.andExpect(jsonPath("$.price", is(product.getPrice())));
	}

	/**
	 * Verify Put inValid Product ID with Authorized Token
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenInvalidProductId_whenUpdateProduct_thenNotUpdatedAndReturn404NotFound() throws IOException, Exception {
		// Data preparation
		Product product = createProduct("Mobile", 500, true);

		product.setName("ABC Mobile");
		product.setPrice(350);

		// Method call and Verification
		mvc.perform(put(API_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(product)))
				.andExpect(status().isNotFound());
	}

	/**
	 * Save Product
	 * 
	 * @param name
	 * @param price
	 * @param available
	 * @return
	 */
	private Product createProduct(String name, double price, boolean available) {
		Product product = new Product(null, name, price, available, "", "", new Date(), new Date());
		return productRepository.saveAndFlush(product);
	}
	
	private ProductRequestDto mockProductRequestDto(String name, double price, boolean available) {
		return new ProductRequestDto(name, price, available, "", "");
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
	
	private void assertProduct(ProductResponseDto productResponseDto, ProductRequestDto productRequestDto) {
		assertNotNull(productResponseDto);
		assertNotNull(productResponseDto.getId());
		assertNotNull(productResponseDto.getName());
	    assertEquals(productResponseDto.getName(), productRequestDto.getName());
	    assertNotNull(productResponseDto.getPrice());
	    assertThat(productResponseDto.getPrice()).isEqualTo(productRequestDto.getPrice());
	    assertEquals(productResponseDto.getDescription(), productRequestDto.getDescription());
	    assertEquals(productResponseDto.getImageURL(), productRequestDto.getImageURL());
	  }
}
