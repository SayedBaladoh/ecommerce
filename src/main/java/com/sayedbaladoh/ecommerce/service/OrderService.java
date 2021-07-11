package com.sayedbaladoh.ecommerce.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.dto.order.OrderDto;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;

public interface OrderService {

	Page<OrderResponseDto> getAllOrders(Pageable pageable);

	Page<OrderResponseDto> getAllOrders(Long userId, Pageable pageable);

	OrderResponseDto getOrder(Long id);

	OrderResponseDto addOrder(Long userId, @NotNull(message = "The order cannot be null.") @Valid OrderDto orderDto);

	OrderDto updateOrder(Long id, @NotNull(message = "The order cannot be null.") @Valid OrderDto orderDto);

	CheckoutSession createCheckoutSession(Long userId,
			@NotNull(message = "The order cannot be null.") @Valid OrderDto orderDto);

	CheckoutSession createCheckoutSession(Long orderId);

}
