package com.sayedbaladoh.ecommerce.dto.order;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemDto;

import lombok.Data;

@Data
public class OrderDto {
	private @NotEmpty @Valid List<OrderItemDto> orderItems;
}
