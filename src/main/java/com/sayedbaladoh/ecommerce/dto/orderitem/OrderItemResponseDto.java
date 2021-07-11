package com.sayedbaladoh.ecommerce.dto.orderitem;

import com.sayedbaladoh.ecommerce.dto.product.ProductResponseDto;

import lombok.Data;

@Data
public class OrderItemResponseDto {
	private int quantity;
	private ProductResponseDto product;
}
