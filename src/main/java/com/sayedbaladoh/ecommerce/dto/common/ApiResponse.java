package com.sayedbaladoh.ecommerce.dto.common;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiResponse {
	private final boolean success;
	private final String message;
	
	public String getTimestamp() {
		return LocalDateTime.now().toString();
	}
}
