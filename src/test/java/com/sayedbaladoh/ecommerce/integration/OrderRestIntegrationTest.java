package com.sayedbaladoh.ecommerce.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayedbaladoh.ecommerce.EcommerceApplication;
import com.sayedbaladoh.ecommerce.dto.common.ErrorItem;
import com.sayedbaladoh.ecommerce.dto.common.ErrorResponse;
import com.sayedbaladoh.ecommerce.dto.order.OrderDto;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;
import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductDto;
import com.sayedbaladoh.ecommerce.dto.user.LoginRequest;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.OrderItem;
import com.sayedbaladoh.ecommerce.model.Product;
import com.sayedbaladoh.ecommerce.model.User;
import com.sayedbaladoh.ecommerce.repository.OrderItemRepository;
import com.sayedbaladoh.ecommerce.repository.OrderRepository;
import com.sayedbaladoh.ecommerce.repository.UserRepository;
import com.sayedbaladoh.ecommerce.util.JsonUtil;

/**
 * Order APIs Integration tests
 * 
 * Test the Order rest web services' integration tests
 * 
 * @author Sayed Baladoh
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = EcommerceApplication.class)
@AutoConfigureMockMvc
public class OrderRestIntegrationTest {

	private final String API_URL = "/orders";
	private final Long INVALID_ID = -99L;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mvc;

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private User user;

	@Before
	public void setUp() {		
		user = createUser("test", "test@test.com", "12345789");
	}
	
	@After
	public void cleanUp() {
		orderItemRepository.deleteAll();
		orderRepository.deleteAll();
		userRepository.deleteAll();
	}
	
	/**
	 * Validate get all orders with list of orders
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrders(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenOrders_whenGetOrders_thenReturnOrdersWithStatus200()
			throws Exception {

		// Data preparation
		Order order1 = createOrder(user);
		Order order2 = createOrder(user);
		createOrderItem(order1, 1l, 5);
		createOrderItem(order1, 2l, 1);
		createOrderItem(order2, 3l, 3);

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
				.andExpect(jsonPath("$.content[0].id").exists())
				.andExpect(jsonPath("$.content[0].id").value(order1.getId()))
				.andExpect(jsonPath("$.content[0].totalOrderPrice", is(greaterThan(0d))))
				.andExpect(jsonPath("$.content[0].numberOfProducts", is(2)))
				.andExpect(jsonPath("$.content[0].status", is(order1.getStatus().toString())))
				.andExpect(jsonPath("$.content[1].id").exists())
				.andExpect(jsonPath("$.content[1].id").value(order2.getId()))
				.andExpect(jsonPath("$.content[1].totalOrderPrice", is(greaterThan(0d))))
				.andExpect(jsonPath("$.content[1].numberOfProducts", is(1)))
				.andExpect(jsonPath("$.content[1].status", is(order2.getStatus().toString())))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}
	
	/**
	 * Validate get all orders with empty list
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrders(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenEmptyOrdersList_whenGetAllOrders_thenReturnOrderPageWithEmptyList()
			throws Exception {
		
		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/orders?page=0&size=15")
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
	 * Validate get order with valid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrder(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenOrder_whenGetOrderById_thenReturnOrderResponse() throws Exception {
		// Data preparation
		Order order1 = createOrder(user);
		Order order2 = createOrder(user);
		createOrderItem(order1, 1l, 5);
		createOrderItem(order1, 2l, 1);
		createOrderItem(order2, 3l, 3);
		
		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/orders/{orderId}" , order1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(order1.getId()))
				.andExpect(jsonPath("$.status").value(order1.getStatus().name()))
				.andExpect(jsonPath("$.totalOrderPrice", is(greaterThan(0d))))
				.andDo(print())
				.andReturn();

		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		 OrderResponseDto orderResponseDto =
		            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponseDto.class);
		 
		 assertOrder(orderResponseDto, order1);			 
	}

	/**
	 * Validate get order with invalid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrder(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenInavlidOrderId_whenGetOrderById_thenReturn404NotFound() throws Exception {
		
		// API call and Verification
		mvc.perform(get(API_URL + "/" + INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	/**
	 * Verify add a valid Order with authorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#addOrder(com.sayedbaladoh.ecommerce.dto.order.OrderDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidOrderRequest_whenAddOrder_thenOrderCreated() throws IOException, Exception {
		// Data preparation	
		String accessToken = obtainAccessToken();
		OrderDto orderDto =  mockOrderDto();
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL)				
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(orderDto))
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").exists())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.message", containsString("Order has been saved with id:")))
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}

	/**
	 * Verify add an invalid Order with authorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#addOrder(com.sayedbaladoh.ecommerce.dto.order.OrderDto)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenInvalidOrderRequest_whenAddOrder_thenOrderNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
		OrderDto orderDto =  mockOrderDto();
		orderDto.setOrderItems(null);

		// API call and Verification
		mvc.perform(post(API_URL)				
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(orderDto)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.id").doesNotExist())
				.andDo(print());
	}

	/**
	 * Verify add an invalid Order with authorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#addOrder(com.sayedbaladoh.ecommerce.dto.order.OrderDto)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test-user",authorities={"USER"})
	@Test
	public void givenValidOrderRequestWithProductNotFound_whenAddOrder_thenOrderNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
		String accessToken = obtainAccessToken();
		
		OrderItemDto orderItemDto = new OrderItemDto();
		orderItemDto.setProduct(new ProductDto(99l));
		orderItemDto.setQuantity(5);		
		
		OrderDto orderDto = new OrderDto();
		orderDto.setOrderItems(List.of(orderItemDto));

		// API call and Verification
		mvc.perform(post(API_URL)				
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(orderDto))
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].message").exists())
				.andExpect(jsonPath("$.errors[0].code", is("Products_Existence")))
				.andExpect(jsonPath("$.errors[0].message", containsString("No products are found with Ids: {#99}")))
				.andDo(print());
	}
	
	/**
	 * Verify add a valid Order with unauthorized user
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#addOrder(com.sayedbaladoh.ecommerce.dto.order.OrderDto)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidOrderRequestAndNotAuthorizedUser_whenAddOrder_thenOrderNotCreatedAndReturnUnauthorized() throws IOException, Exception {
		// Data preparation
		OrderDto orderDto =  mockOrderDto();

		// Method call and Verification
		mvc.perform(post(API_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(orderDto)))
				.andExpect(status().isUnauthorized())
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.id").doesNotExist());
	}
	
	/**
	 * Verify checkout a valid Order
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenValidOrder_whenCheckoutOrder_thenOrderCheckoutSessionCreated() throws IOException, Exception {
		// Data preparation
				Order order = createOrder(user);
				createOrderItem(order, 1l, 2);
				createOrderItem(order, 3l, 1);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", order.getId())				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderId").exists())
				.andExpect(jsonPath("$.orderId", is(order.getId().intValue())))
				.andExpect(jsonPath("$.sessionId").exists())
				.andExpect(jsonPath("$.sessionId").isNotEmpty())
				.andExpect(jsonPath("$.paymentStatus").exists())
				.andExpect(jsonPath("$.paymentStatus", is("unpaid")))
				.andExpect(jsonPath("$.url").exists())	
				.andExpect(jsonPath("$.url", containsString("https://checkout.stripe.com/pay/cs_test_")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}
	
	/**
	 * Verify checkout an invalid order Id
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenInvalidOrderId_whenCheckoutOrder_thenReturn404NotFound() throws IOException, Exception {
	
		// API call and Verification
		mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", INVALID_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	
	/**
	 * Verify checkout an order with basket item is not available
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenOrderWithItemsIsNotAvailable_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
				Order order = createOrder(user);
				createOrderItem(order, 1l, 1);
				createOrderItem(order, 2l, 1);
				createOrderItem(order, 6l, 3);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", order.getId())				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("BASKET_ITEMS_AVAILABILITY")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("These basket items are not available: {#2- LabTop, #6- Phone}")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}

	/**
	 * Verify checkout an order with user fraud, user's order basket has more than 1500 money value
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenOrderWithUserFraud_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
				Order order = createOrder(user);
				createOrderItem(order, 1l, 5);
				createOrderItem(order, 3l, 1);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", order.getId())				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("USER_FRAUD")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("User is fraud, the fraud user's order basket has more than 1500 money value.")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}
	
	/**
	 * Verify checkout an order with total basket money less than 100
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenOrderWithInvalidTotalBasketMony_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
				Order order = createOrder(user);
				createOrderItem(order, 4l, 5);
				createOrderItem(order, 7l, 1);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", order.getId())				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("TOTAL_BASKET_MONEY")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("The total basket money value less than 100.")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
	}
	
	/**
	 * Verify checkout an order with  basket item is not available and user fraud, user's order basket has more than 1500 money value
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@WithMockUser(username="test", authorities={"USER"})
	@Test
	public void givenOrderWithItemIsNotAvailableAndUserFraud_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
				Order order = createOrder(user);
				createOrderItem(order, 1l, 5);
				createOrderItem(order, 2l, 1);
				createOrderItem(order, 3l, 1);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post(API_URL+ "/{orderId}/checkout/sessions", order.getId())				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		ErrorResponse errors =
	            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

		assertThat(errors.getErrors())
		.hasSize(2)
		.extracting(ErrorItem::getCode)
		.contains("USER_FRAUD", "BASKET_ITEMS_AVAILABILITY");
		assertThat(errors.getErrors())
		.extracting(ErrorItem::getMessage)
		.contains("User is fraud, the fraud user's order basket has more than 1500 money value.", "These basket items are not available: {#2- LabTop}");
	}

	/**
	 * Save Order
	 * 
	 * @param user
	 * @return
	 */
	private Order createOrder(User user) {
		Order order = new Order();
		order.setUser(user);
		order.setStatus(OrderStatus.NEW);
		return orderRepository.saveAndFlush(order);
	}
	
	private OrderItem createOrderItem(Order order, Long ProductId, int quantity) {
		OrderItem item = new OrderItem();
		item.setOrder(order);
		item.setProduct(new Product(ProductId));
		item.setQuantity(quantity);
		return orderItemRepository.save(item);
	}
	
	private User createUser(String name, String email, String password) {
		User user = new User(name, email, passwordEncoder.encode(password));		
		return userRepository.save(user);
	}
	
	/**
	 * Get access token
	 * 
	 * @return
	 * @throws Exception
	 */
	private String obtainAccessToken() throws Exception {
		// Data preparation
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(user.getEmail());
		loginRequest.setPassword("12345789");
		// Method call and Verification
		ResultActions result = mvc.perform(post("/auth/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtil.toJson(loginRequest)))
				.andExpect(status().isOk());

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("accessToken").toString();
	}
	
	private OrderDto mockOrderDto() {
		OrderItemDto orderItemDto = new OrderItemDto();
		orderItemDto.setProduct(new ProductDto(1l));
		orderItemDto.setQuantity(5);
		
		OrderDto orderDto = new OrderDto();
		orderDto.setOrderItems(List.of(orderItemDto));
		return orderDto;
	}

	private void assertOrder(OrderResponseDto orderDto, Order order) {
		assertNotNull(orderDto);
		assertNotNull(orderDto.getId());
		assertEquals(orderDto.getId(), order.getId());
		assertNotNull(orderDto.getStatus());
	    assertEquals(orderDto.getStatus().name(), order.getStatus().name());
	    assertNotNull(orderDto.getTotalOrderPrice());
	    assertThat(orderDto.getTotalOrderPrice()).isGreaterThan(0d);
	    assertThat(orderDto.getNumberOfProducts()).isGreaterThan(0);
	  }
}
