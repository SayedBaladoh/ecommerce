package com.sayedbaladoh.ecommerce.dto.product;

import lombok.Data;

@Data
public class ProductResponseDto {

	private Long id;
	private String name;
	private double price;
	private boolean available;
	private String imageURL;
	private String description;
}