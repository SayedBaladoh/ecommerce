package com.sayedbaladoh.ecommerce.dto.orderitem;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.sayedbaladoh.ecommerce.dto.product.ProductDto;

import lombok.Data;

@Data
public class OrderItemDto {

	private @NotNull @Min(1) int quantity;
	private @Valid ProductDto product;
}
