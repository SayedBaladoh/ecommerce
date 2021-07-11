/**
 * 
 */
package com.sayedbaladoh.ecommerce.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.After;
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

import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;
import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;
import com.sayedbaladoh.ecommerce.exception.ResourceNotFoundException;
import com.sayedbaladoh.ecommerce.exception.ValidationViolationException;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.User;
import com.sayedbaladoh.ecommerce.repository.OrderRepository;
import com.sayedbaladoh.ecommerce.service.PaymentGateway;
import com.sayedbaladoh.ecommerce.util.ObjectMapperHelper;
import com.sayedbaladoh.ecommerce.validations.ValidationContext;
import com.sayedbaladoh.ecommerce.validations.ValidationViolation;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

/**
 * Order service unit tests
 * 
 * Test the Order service logic
 * 
 * @author Sayed Baladoh
 *
 */
@RunWith(SpringRunner.class)
public class OrderServiceImplTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ObjectMapperHelper objectMapperHelper;
	@Mock
	private ValidationContext validationContext;
	@Mock
	private PaymentGateway paymentGateway;
	@InjectMocks
	private OrderServiceImpl orderService;
	
	@After
	public void setUp() {
		reset(orderRepository);
		reset(objectMapperHelper);
	}

	/**
	 * Validate get all orders with list of orders
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#getAllOrders(org.springframework.data.domain.Pageable)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrders_whenGetOrders_thenReturnOrdersWithStatus200()
			throws Exception {

		// Data preparation
		User user = new User(1l);
		Order order1 = mockOrder(user);
		Order order2 = mockOrder(user);
		Order order3 = mockOrder(user);
		
		Page<Order> mockedOrdersPage = new PageImpl<Order>(List.of(order1, order2, order3));
		
		ProductResponseDto productDto1 = mockProductResponseDto(1l, "Mobile", 150, true);
		ProductResponseDto productDto2 = mockProductResponseDto(1l, "Labtop", 200, true);

		OrderItemResponseDto orderItem1 = mockOrderItemResponseDto(5, productDto1);
		OrderItemResponseDto orderItem2 = mockOrderItemResponseDto(1, productDto2);
		 
		OrderResponseDto orderDto1 = mockOrderResponseDto(1l, OrderStatus.NEW, List.of(orderItem1, orderItem2), 650.0);
		OrderResponseDto orderDto2 = mockOrderResponseDto(1l, OrderStatus.NEW, List.of(orderItem1, orderItem2), 650.0);
		Page<OrderResponseDto> mockedOrderResponseDtoPage = new PageImpl<OrderResponseDto>(List.of(orderDto1, orderDto2));
		
		Mockito.when(orderRepository.findAll(any(Pageable.class)))
				.thenReturn(mockedOrdersPage);
		Mockito.when(objectMapperHelper.mapAll(mockedOrdersPage, OrderResponseDto.class))
				.thenReturn(mockedOrderResponseDtoPage);

		// Method call
		Page<OrderResponseDto> ordersPage = orderService.getAllOrders(PageRequest.of(0, 5));

		// Verification
		assertThat(ordersPage).isNotNull();
		assertThat(ordersPage.getContent())
			.hasSize(2)
			.extracting(OrderResponseDto::getId)
			.contains(orderDto1.getId(),
					orderDto1.getId());
		assertEquals(ordersPage.getNumber(), 0);
		assertEquals(ordersPage.getNumberOfElements(), 2);
		assertEquals(ordersPage.getTotalElements(), 2);
		assertEquals(ordersPage.getTotalPages(), 1);

		Mockito.verify(orderRepository, Mockito.times(1)).findAll(PageRequest.of(0, 5));
		Mockito.verifyNoMoreInteractions(orderRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).mapAll(mockedOrdersPage, OrderResponseDto.class);
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
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
		Page<Order> mockedOrdersPage = new PageImpl<Order>(Collections.emptyList());
		PageImpl<OrderResponseDto> orderResponseDtoPage = new PageImpl<OrderResponseDto>(Collections.emptyList());
		
		Mockito.when(orderRepository.findAll(any(Pageable.class)))
				.thenReturn(mockedOrdersPage);
		Mockito.when(objectMapperHelper.mapAll(mockedOrdersPage, OrderResponseDto.class))
				.thenReturn(orderResponseDtoPage);
		
		// Method call
		Page<OrderResponseDto> ordersPage = orderService.getAllOrders(PageRequest.of(0, 5));

		// Verification
		assertNotNull(ordersPage);
		assertThat(ordersPage.getContent()).hasSize(0);
		assertEquals(ordersPage.getNumber(), 0);
		assertEquals(ordersPage.getNumberOfElements(), 0);
		assertEquals(ordersPage.getTotalElements(), 0);
		assertEquals(ordersPage.getTotalPages(), 1);
		
		Mockito.verify(orderRepository, Mockito.times(1)).findAll(PageRequest.of(0, 5));
		Mockito.verifyNoMoreInteractions(orderRepository);
		Mockito.verify(objectMapperHelper, Mockito.times(1)).mapAll(mockedOrdersPage, OrderResponseDto.class);
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}
	
	/**
	 * Validate get order with valid Id
	 * 
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#getOrder(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenOrder_whenGetOrderById_thenReturnOrderResponse() throws Exception {
		// Data preparation
		User user = new User(1l);
		Order order1 = mockOrder(user);
		order1.setId(1l);
				
		ProductResponseDto productDto1 = mockProductResponseDto(1l, "Mobile", 150, true);
		ProductResponseDto productDto2 = mockProductResponseDto(1l, "Labtop", 200, true);

		OrderItemResponseDto orderItem1 = mockOrderItemResponseDto(5, productDto1);
		OrderItemResponseDto orderItem2 = mockOrderItemResponseDto(1, productDto2);
		 
		OrderResponseDto orderDto1 = mockOrderResponseDto(1l, OrderStatus.NEW, List.of(orderItem1, orderItem2), 650.0);
		
		Mockito.when(orderRepository.findById(order1.getId()))
				.thenReturn(Optional.of(order1));
		Mockito.when(objectMapperHelper.map(any(Order.class), eq(OrderResponseDto.class)))
				.thenReturn(orderDto1);
		
		// Method call
		OrderResponseDto orderResponseDto = orderService.getOrder(order1.getId());

		// Verification
		assertThat(orderResponseDto).isNotNull();
		assertEquals(orderResponseDto.getId(), order1.getId());
		assertEquals(orderResponseDto.getStatus(), order1.getStatus());
		assertEquals(orderResponseDto.getNumberOfProducts(), orderDto1.getNumberOfProducts());

		Mockito.verify(orderRepository, Mockito.times(1)).findById(order1.getId());
		Mockito.verifyNoMoreInteractions(orderRepository);		
		Mockito.verify(objectMapperHelper, Mockito.times(1)).map(any(Order.class), eq(OrderResponseDto.class));
		Mockito.verifyNoMoreInteractions(objectMapperHelper);
	}
	
	/**
	 * Validate get order by Id using invalid Id
	 * 
 	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#getOrder(java.lang.Long)}.
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void givenInvalidOrderId_whenGetOrder_thenOrderShouldNotBeFound() {
		// Data preparation
		final Long INVALID_ID = 99l;
		
		// Method call
		orderService.getOrder(INVALID_ID);

		// Verification
		Mockito.verify(orderRepository, Mockito.times(1)).findById(INVALID_ID);
		Mockito.verifyNoMoreInteractions(orderRepository);
		Mockito.verifyZeroInteractions(objectMapperHelper);
	}
	
	/**
	 * Verify checkout a valid Order
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#createCheckoutSession(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void givenValidOrder_whenCreateCheckoutSession_thenOrderCheckoutSessionCreated() throws IOException, Exception {
		// Data preparation
		User user = new User(1l);
		Order mockedOrder = mockOrder(user);
		mockedOrder.setId(1l);

		CheckoutSession mockedCheckoutSession = mockCheckoutSession(mockedOrder.getId(), "125489515dd55ds5ds5fADASD", "unpaid", "https://checkout.stripe.com/pay/cs_test_123454785199");
		
		Mockito.when(orderRepository.findById(mockedOrder.getId()))
				.thenReturn(Optional.of(mockedOrder));
		Mockito.when(validationContext.execute(ValidationDomain.CHECK_OUT, mockedOrder))
				.thenReturn(Collections.emptySet());
		Mockito.when(paymentGateway.createCheckoutSession(mockedOrder))
				.thenReturn(mockedCheckoutSession);
				
		// Method call
		CheckoutSession checkoutSession = orderService.createCheckoutSession(mockedOrder.getId());
				
		// Verification
		assertThat(checkoutSession).isNotNull();
		assertEquals(checkoutSession.getOrderId(), mockedOrder.getId().longValue());
		assertEquals(checkoutSession.getPaymentStatus(), mockedCheckoutSession.getPaymentStatus());
		assertEquals(checkoutSession.getSessionId(), mockedCheckoutSession.getSessionId());
		assertEquals(checkoutSession.getUrl(), mockedCheckoutSession.getUrl());

		Mockito.verify(orderRepository, Mockito.times(1)).findById( mockedOrder.getId());
		Mockito.verify(orderRepository, Mockito.times(1)).save(mockedOrder);
		Mockito.verifyNoMoreInteractions(orderRepository);
		verify(validationContext, times(1)).execute(ValidationDomain.CHECK_OUT, mockedOrder);
		Mockito.verifyNoMoreInteractions(validationContext);
		verify(paymentGateway, times(1)).createCheckoutSession(mockedOrder);
		Mockito.verifyNoMoreInteractions(paymentGateway);
	}
	
	/**
	 * Verify checkout an order with basket item is not available
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#createCheckoutSession(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ValidationViolationException.class)
	public void givenOrderWithItemsIsNotAvailable_whenCreateCheckoutSession_thenOrderCheckoutSessionIsNotCreated() throws IOException, Exception {
		// Data preparation
		User user = new User(1l);
		Order mockedOrder = mockOrder(user);
		mockedOrder.setId(1l);

		CheckoutSession mockedCheckoutSession = mockCheckoutSession(mockedOrder.getId(), "125489515dd55ds5ds5fADASD", "unpaid", "https://checkout.stripe.com/pay/cs_test_123454785199");
		
		Mockito.when(orderRepository.findById(mockedOrder.getId()))
				.thenReturn(Optional.of(mockedOrder));
		Mockito.when(validationContext.execute(ValidationDomain.CHECK_OUT, mockedOrder))
				.thenReturn(Set.of(new ValidationViolation(ValidationType.BASKET_ITEMS_AVAILABILITY, "These basket items are not available: {#2- LabTop, #6- Phone}")));
		Mockito.when(paymentGateway.createCheckoutSession(mockedOrder))
				.thenReturn(mockedCheckoutSession);
				
		// Method call
		orderService.createCheckoutSession(mockedOrder.getId());
	}

	/**
	 * Verify checkout an order with user fraud, user's order basket has more than 1500 money value
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#createCheckoutSession(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ValidationViolationException.class)
	public void givenOrderWithOrderWithUserFraud_whenCreateCheckoutSession_thenOrderCheckoutSessionIsNotCreated() throws IOException, Exception {
		// Data preparation
		User user = new User(1l);
		Order mockedOrder = mockOrder(user);
		mockedOrder.setId(1l);

		CheckoutSession mockedCheckoutSession = mockCheckoutSession(mockedOrder.getId(), "125489515dd55ds5ds5fADASD", "unpaid", "https://checkout.stripe.com/pay/cs_test_123454785199");
		
		Mockito.when(orderRepository.findById(mockedOrder.getId()))
				.thenReturn(Optional.of(mockedOrder));
		Mockito.when(validationContext.execute(ValidationDomain.CHECK_OUT, mockedOrder))
				.thenReturn(Set.of(new ValidationViolation(ValidationType.USER_FRAUD, "User is fraud, the fraud user's order basket has more than 1500 money value.")));
		Mockito.when(paymentGateway.createCheckoutSession(mockedOrder))
				.thenReturn(mockedCheckoutSession);
				
		// Method call
		orderService.createCheckoutSession(mockedOrder.getId());
	}
	
	/**
	 * Verify checkout an order with total basket money less than 100
	 *
	 * Test method for {@link com.sayedbaladoh.ecommerce.service.impl.OrderServiceImpl#createCheckoutSession(java.lang.Long)}.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ValidationViolationException.class)
	public void givenOrderWithInvalidTotalBasketMony_whenCreateCheckoutSession_thenOrderCheckoutSessionIsNotCreated() throws IOException, Exception {
		// Data preparation
		User user = new User(1l);
		Order mockedOrder = mockOrder(user);
		mockedOrder.setId(1l);

		CheckoutSession mockedCheckoutSession = mockCheckoutSession(mockedOrder.getId(), "125489515dd55ds5ds5fADASD", "unpaid", "https://checkout.stripe.com/pay/cs_test_123454785199");
		
		Mockito.when(orderRepository.findById(mockedOrder.getId()))
				.thenReturn(Optional.of(mockedOrder));
		Mockito.when(validationContext.execute(ValidationDomain.CHECK_OUT, mockedOrder))
				.thenReturn(Set.of(new ValidationViolation(ValidationType.TOTAL_BASKET_MONEY, "The total basket money value less than 100.")));
		Mockito.when(paymentGateway.createCheckoutSession(mockedOrder))
				.thenReturn(mockedCheckoutSession);
				
		// Method call
		orderService.createCheckoutSession(mockedOrder.getId());
	}
	
	private Order mockOrder(User user) {
		Order order = new Order();
		order.setUser(user);
		order.setStatus(OrderStatus.NEW);
		return order;
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

}
