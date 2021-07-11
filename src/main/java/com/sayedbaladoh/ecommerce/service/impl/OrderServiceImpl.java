package com.sayedbaladoh.ecommerce.service.impl;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.dto.order.OrderDto;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;
import com.sayedbaladoh.ecommerce.exception.ResourceNotFoundException;
import com.sayedbaladoh.ecommerce.exception.ValidationViolationException;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.OrderItem;
import com.sayedbaladoh.ecommerce.model.User;
import com.sayedbaladoh.ecommerce.repository.OrderRepository;
import com.sayedbaladoh.ecommerce.service.OrderItemService;
import com.sayedbaladoh.ecommerce.service.OrderService;
import com.sayedbaladoh.ecommerce.service.PaymentGateway;
import com.sayedbaladoh.ecommerce.util.ObjectMapperHelper;
import com.sayedbaladoh.ecommerce.validations.ValidationContext;
import com.sayedbaladoh.ecommerce.validations.ValidationViolation;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final ObjectMapperHelper objectMapperHelper;
	private final PaymentGateway paymentGateway;
	private final OrderItemService orderItemService;
	private final ValidationContext validationContext;

	@Override
	public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
		return objectMapperHelper.mapAll(orderRepository.findAll(pageable), OrderResponseDto.class);
	}

	@Override
	public Page<OrderResponseDto> getAllOrders(Long userId, Pageable pageable) {
		return objectMapperHelper.mapAll(orderRepository.findAllByUserId(userId, pageable), OrderResponseDto.class);
	}

	@Override
	public OrderResponseDto getOrder(Long id) {
		return objectMapperHelper.map(get(id), OrderResponseDto.class);
	}

	@Transactional
	@Override
	public OrderResponseDto addOrder(Long userId, OrderDto orderDto) {
		// Apply validation policies for saving order
		// for example validate products existence for the order
		Set<ValidationViolation> violations = validationContext.execute(ValidationDomain.ORDER_SAVE, orderDto);
		if (!violations.isEmpty())
			throw new ValidationViolationException(violations);

		Order order = objectMapperHelper.map(orderDto, Order.class);
		order.setUser(new User(userId));
		order.setStatus(OrderStatus.NEW);
		order = orderRepository.save(order);

		List<OrderItem> orderItems = orderItemService.addOrderItems(order, orderDto.getOrderItems());
		order.setOrderItems(orderItems);

		return objectMapperHelper.map(order, OrderResponseDto.class);
	}

	@Override
	public OrderDto updateOrder(Long id, OrderDto orderDto) {
		Order order = objectMapperHelper.map(orderDto, get(id));
		return save(order);
	}

	@Transactional
	public CheckoutSession createCheckoutSession(Long userId, OrderDto orderDto) {
		OrderResponseDto orderResponse = addOrder(userId, orderDto);
		return createCheckoutSession(orderResponse.getId());
	}

	@Transactional
	public CheckoutSession createCheckoutSession(Long orderId) {

		// Get order
		Order order = get(orderId);

		// Validate order
		// Apply validation policies for checking out order
		Set<ValidationViolation> violations = validationContext.execute(ValidationDomain.CHECK_OUT, order);
		if (!violations.isEmpty())
			throw new ValidationViolationException(violations);

		// Get checkout session
		CheckoutSession checkoutSession = null;
		checkoutSession = paymentGateway.createCheckoutSession(order);

		// Update order status
		order.setStatus(OrderStatus.CHECKOUT_SESSION_RETRIEVED);
		order.setSessionId(checkoutSession.getSessionId());
		orderRepository.save(order);

		return checkoutSession;

	}

	private Order get(long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(String.format("Order with id: %d is not found.", id)));
	}

	private OrderDto save(Order order) {
		return objectMapperHelper.map(orderRepository.save(order), OrderDto.class);
	}
}