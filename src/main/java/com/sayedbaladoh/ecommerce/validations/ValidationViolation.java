package com.sayedbaladoh.ecommerce.validations;

import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ValidationViolation {

	private ValidationType type;

	private String message;

}
