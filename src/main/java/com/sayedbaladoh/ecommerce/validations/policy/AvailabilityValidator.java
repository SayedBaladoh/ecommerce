package com.sayedbaladoh.ecommerce.validations.policy;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.validations.ValidationStrategy;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

@Component
public class AvailabilityValidator implements ValidationStrategy {

	@Value("${checkout.constraints.basket.items.availability.message}")
	private String messagePreFix;

	private String message;

	@Override
	public ValidationDomain getDomain() {
		return ValidationDomain.CHECK_OUT;
	}

	@Override
	public ValidationType getType() {
		return ValidationType.BASKET_ITEMS_AVAILABILITY;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public <T> boolean validate(T o) {
		String notAvailableItems = ((Order) o).getOrderItems().stream().filter(item -> !item.getProduct().isAvailable())
				.map(item -> "#" + item.getProduct().getId() + "- " + item.getProduct().getName())
				.collect(Collectors.joining(", ", "{", "}"));
		message = messagePreFix + notAvailableItems;
		return notAvailableItems.equals("{}");
	}

}
