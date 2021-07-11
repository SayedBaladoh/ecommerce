package com.sayedbaladoh.ecommerce.validations.policy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.validations.ValidationStrategy;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

@Component
public class UserFraudValidator implements ValidationStrategy {

	@Value("${checkout.constraints.user.fraud.message}")
	private String message;

	@Override
	public ValidationDomain getDomain() {
		return ValidationDomain.CHECK_OUT;
	}

	@Override
	public ValidationType getType() {
		return ValidationType.USER_FRAUD;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public <T> boolean validate(T o) {
		return ((Order) o).getTotalOrderPrice() <= 1500;
	}
}
