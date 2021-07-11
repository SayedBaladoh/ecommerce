/**
 * 
 */
package com.sayedbaladoh.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;
import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;
import com.sayedbaladoh.ecommerce.exception.ResourceNotFoundException;
import com.sayedbaladoh.ecommerce.exception.ValidationViolationException;
import com.sayedbaladoh.ecommerce.service.OrderService;
import com.sayedbaladoh.ecommerce.validations.ValidationViolation;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

/**
 * Order controller unit tests
 * 
 * Test the Order rest APIs unit tests
 * 
 * @author Sayed Baladoh
 */
@RunWith(SpringRunner.class)
@WebMvcTest(
		value = OrderController.class,
		secure = false)
@EnableSpringDataWebSupport
public class OrderControllerTest {

	@Autowired
	private MockMvc mvc;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@MockBean
	private OrderService orderService;
	
	@Before
	public void setUp() {
		reset(orderService);
	}

	/**
	 * Validate get all orders with list of orders
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrders(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrders_whenGetOrders_thenReturnOrdersWithStatus200()
			throws Exception {

		// Data preparation
		 ProductResponseDto productDto1 = mockProductResponseDto(1l, "Mobile", 150, true);
		 ProductResponseDto productDto2 = mockProductResponseDto(1l, "Labtop", 200, true);

		 OrderItemResponseDto orderItem1 = mockOrderItemResponseDto(5, productDto1);
		 OrderItemResponseDto orderItem2 = mockOrderItemResponseDto(1, productDto2);
		 
		 OrderResponseDto orderDto = mockOrderResponseDto(1l, OrderStatus.NEW, List.of(orderItem1, orderItem2), 650.0);
		PageImpl<OrderResponseDto> orderResponseDtoPage = new PageImpl<OrderResponseDto>(
				List.of(orderDto));
		
		given(orderService.getAllOrders(any(Pageable.class)))
				.willReturn(orderResponseDtoPage);

		// API call and Verification
		MvcResult mvcResult = mvc.perform(get("/orders?page=0&size=15")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(1)))
				.andExpect(jsonPath("$.totalElements", is(1)))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.content", hasSize(equalTo(1))))
				.andExpect(jsonPath("$.content[0].id").exists())
				.andExpect(jsonPath("$.content[0].id").value(orderDto.getId()))
				.andExpect(jsonPath("$.content[0].totalOrderPrice", is(orderDto.getTotalOrderPrice())))
				.andExpect(jsonPath("$.content[0].numberOfProducts", is(orderDto.getNumberOfProducts())))
				.andExpect(jsonPath("$.content[0].status", is(orderDto.getStatus().toString())))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(orderService, times(1)).getAllOrders(any(Pageable.class));
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Validate get all orders with empty list
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrders(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenEmptyOrdersList_whenGetAllOrders_thenReturnOrderPageWithEmptyList()
			throws Exception {
		
		// Data preparation
		PageImpl<OrderResponseDto> orderResponseDtoPage = new PageImpl<OrderResponseDto>(Collections.emptyList());
		
		given(orderService.getAllOrders(any(Pageable.class)))
				.willReturn(orderResponseDtoPage);
		
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
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(orderService, times(1)).getAllOrders(any(Pageable.class));
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Validate get order with valid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrder(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrder_whenGetOrderById_thenReturnOrderResponse() throws Exception {
		// Data preparation
		ProductResponseDto productDto1 = mockProductResponseDto(1l, "Mobile", 150, true);
		ProductResponseDto productDto2 = mockProductResponseDto(2l, "Labtop", 200, true);

		OrderItemResponseDto orderItem1 = mockOrderItemResponseDto(5, productDto1);
		OrderItemResponseDto orderItem2 = mockOrderItemResponseDto(1, productDto2);
		 
		OrderResponseDto orderDto = mockOrderResponseDto(1l, OrderStatus.NEW, List.of(orderItem1, orderItem2), 650.0);

		given(orderService.getOrder(orderDto.getId()))
				.willReturn(orderDto);
		
		//API call and Verification
		MvcResult mvcResult = mvc.perform(get("/orders/{orderId}" , orderDto.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(orderDto.getId()))
				.andExpect(jsonPath("$.status").value(orderDto.getStatus().name()))
				.andExpect(jsonPath("$.totalOrderPrice", is(orderDto.getTotalOrderPrice())))
				.andExpect(jsonPath("$.numberOfProducts", is(orderDto.getNumberOfProducts())))
				.andDo(print())
				.andReturn();

		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		OrderResponseDto orderResponseDto =
		            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderResponseDto.class);
		 
		assertOrderResponse(orderResponseDto, orderDto);	
		verify(orderService, times(1)).getOrder(orderDto.getId());
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Validate get order with invalid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#getOrder(java.lang.Long)}. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrderAndInavlidOrderId_whenGetOrderById_thenReturn404NotFound() throws Exception {
				
		// Data preparation
		long invalidOrderId = 55l;

		given(orderService.getOrder(invalidOrderId))
				.willThrow(new ResourceNotFoundException());

		// Verification
		this.mvc.perform(get("/orders/{orderId}", invalidOrderId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andDo(print());
		verify(orderService, times(1)).getOrder(invalidOrderId);
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Verify checkout a valid Order
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidOrder_whenCheckoutOrder_thenOrderCheckoutSessionCreated() throws IOException, Exception {
		// Data preparation
		Long orderId = 1l;
		CheckoutSession checkoutSession = mockCheckoutSession(orderId, "125489515dd55ds5ds5fADASD", "unpaid", "https://checkout.stripe.com/pay/cs_test_123454785199");
	
		given(orderService.createCheckoutSession(any(Long.class)))
		.willReturn(checkoutSession);
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post("/orders/{orderId}/checkout/sessions", orderId)				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderId").exists())
				.andExpect(jsonPath("$.orderId", is(orderId.intValue())))
				.andExpect(jsonPath("$.sessionId").exists())
				.andExpect(jsonPath("$.sessionId").isNotEmpty())
				.andExpect(jsonPath("$.paymentStatus").exists())
				.andExpect(jsonPath("$.paymentStatus", is(checkoutSession.getPaymentStatus())))
				.andExpect(jsonPath("$.url").exists())	
				.andExpect(jsonPath("$.url", containsString("https://checkout.stripe.com/pay/cs_test_")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		
		verify(orderService, times(1)).createCheckoutSession(orderId);
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Verify checkout an invalid order Id
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */	
	@Test
	public void givenInvalidOrderId_whenCheckoutOrder_thenReturn404NotFound() throws IOException, Exception {
	
		long invalidOrderId = 55l;
		given(orderService.createCheckoutSession(invalidOrderId))
				.willThrow(new ResourceNotFoundException());
		
		// API call and Verification
		mvc.perform(post("/orders/{orderId}/checkout/sessions", invalidOrderId)
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
	@Test
	public void givenOrderWithItemsIsNotAvailable_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
		Long orderId = 1l;
	
		given(orderService.createCheckoutSession(orderId))
		.willThrow(new ValidationViolationException(Set.of(new ValidationViolation(ValidationType.BASKET_ITEMS_AVAILABILITY, "These basket items are not available: {#2- LabTop, #6- Phone}"))));
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post("/orders/{orderId}/checkout/sessions", orderId)				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("BASKET_ITEMS_AVAILABILITY")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("These basket items are not available: {#2- LabTop, #6- Phone}")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(orderService, times(1)).createCheckoutSession(orderId);
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Verify checkout an order with user fraud, user's order basket has more than 1500 money value
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */	
	@Test
	public void givenOrderWithUserFraud_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
		Long orderId = 1l;
	
		given(orderService.createCheckoutSession(orderId))
		.willThrow(new ValidationViolationException(Set.of(new ValidationViolation(ValidationType.USER_FRAUD, "User is fraud, the fraud user's order basket has more than 1500 money value."))));
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post("/orders/{orderId}/checkout/sessions", orderId)				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("USER_FRAUD")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("User is fraud, the fraud user's order basket has more than 1500 money value.")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(orderService, times(1)).createCheckoutSession(orderId);
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	/**
	 * Verify checkout an order with total basket money less than 100
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.controller.OrderController#checkoutOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrderWithInvalidTotalBasketMony_whenCheckoutOrder_thenOrderCheckoutSessionIsNotCreatedAndReturnBadRequest() throws IOException, Exception {
		// Data preparation
		Long orderId = 1l;
	
		given(orderService.createCheckoutSession(orderId))
		.willThrow(new ValidationViolationException(Set.of(new ValidationViolation(ValidationType.TOTAL_BASKET_MONEY, "The total basket money value less than 100."))));
				
		// API call and Verification
		MvcResult mvcResult = mvc.perform(post("/orders/{orderId}/checkout/sessions", orderId)				
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").exists())
				.andExpect(jsonPath("$.errors[0].code", is("TOTAL_BASKET_MONEY")))
				.andExpect(jsonPath("$.errors[0].message").exists())				
				.andExpect(jsonPath("$.errors[0].message", containsString("The total basket money value less than 100.")))
				.andDo(print())
				.andReturn();
		
		assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
		verify(orderService, times(1)).createCheckoutSession(orderId);
		Mockito.verifyNoMoreInteractions(orderService);
	}
	
	private ProductResponseDto mockProductResponseDto(Long id, String name, double price, boolean available) {
		ProductResponseDto productResponseDto = new ProductResponseDto();
		productResponseDto.setId(id);
		productResponseDto.setName(name);
		productResponseDto.setPrice(price);
		productResponseDto.setAvailable(available);
		return productResponseDto;
	}
	
	private OrderItemResponseDto mockOrderItemResponseDto(int quantity, ProductResponseDto productDto) {
		OrderItemResponseDto orderItemDto = new OrderItemResponseDto();
		orderItemDto.setProduct(productDto);;
		orderItemDto.setQuantity(quantity);
		return orderItemDto;
	}
	
	private OrderResponseDto mockOrderResponseDto(Long id, OrderStatus orderStatus, List<OrderItemResponseDto> orderItems, double totalPrice) {
		OrderResponseDto orderResponse = new OrderResponseDto();
		orderResponse.setId(id);
		orderResponse.setStatus(orderStatus);
		orderResponse.setNumberOfProducts(orderItems.size());
		orderResponse.setOrderItems(orderItems);
		orderResponse.setTotalOrderPrice(totalPrice);
		return orderResponse;
	}
	
	private CheckoutSession mockCheckoutSession(long orderId, String sessionId, String paymentStatus, String url){
		return CheckoutSession.builder()
				.orderId(orderId)
				.sessionId(sessionId)
				.paymentStatus(paymentStatus)
				.url(url)
				.build();		
	}
	
	private void assertOrderResponse(OrderResponseDto returnedOrderDto, OrderResponseDto mockedOrderDto) {
		assertNotNull(returnedOrderDto);
		assertNotNull(returnedOrderDto.getId());
		assertEquals(returnedOrderDto.getId(), mockedOrderDto.getId());
		assertNotNull(returnedOrderDto.getStatus());
	    assertEquals(returnedOrderDto.getStatus().name(), mockedOrderDto.getStatus().name());
	    assertNotNull(returnedOrderDto.getTotalOrderPrice());
	    assertThat(returnedOrderDto.getTotalOrderPrice()).isEqualTo(mockedOrderDto.getTotalOrderPrice());
	    assertEquals(returnedOrderDto.getNumberOfProducts(), mockedOrderDto.getNumberOfProducts());
	  }
	
}
