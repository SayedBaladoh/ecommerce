package com.sayedbaladoh.ecommerce.dto.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

	private List<ErrorItem> errors = new ArrayList<>();

	public void addError(ErrorItem error) {
		this.errors.add(error);
	}
}
