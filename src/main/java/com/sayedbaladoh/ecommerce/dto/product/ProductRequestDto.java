package com.sayedbaladoh.ecommerce.dto.product;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductRequestDto {

	private @NotNull String name;
	private @NotNull double price;
	private boolean available;
	private String imageURL;
	private String description;

}