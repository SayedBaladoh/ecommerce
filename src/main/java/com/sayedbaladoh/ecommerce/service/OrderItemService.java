package com.sayedbaladoh.ecommerce.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemDto;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.OrderItem;

@Validated
public interface OrderItemService {

	OrderItem create(@NotNull(message = "The products for order cannot be null.") @Valid OrderItem orderItem);

	List<OrderItem> addOrderItems(Order order, List<OrderItemDto> orderItemDtos);
}
