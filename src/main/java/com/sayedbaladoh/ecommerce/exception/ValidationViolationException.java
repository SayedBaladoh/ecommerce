package com.sayedbaladoh.ecommerce.exception;

import java.util.Set;

import javax.validation.ValidationException;

import com.sayedbaladoh.ecommerce.validations.ValidationViolation;

public class ValidationViolationException extends ValidationException {

	private static final long serialVersionUID = 1592874951175399L;
	private final Set<ValidationViolation> validationViolations;

	public ValidationViolationException(String message, Set<ValidationViolation> validationViolations) {
		super(message);
		this.validationViolations = validationViolations;
	}

	public ValidationViolationException(Set<ValidationViolation> validationViolations) {
		super();
		this.validationViolations = validationViolations;
	}

	public Set<ValidationViolation> getConstraintViolations() {
		return validationViolations;
	}
}