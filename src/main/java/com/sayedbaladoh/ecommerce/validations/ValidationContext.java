package com.sayedbaladoh.ecommerce.validations;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ValidationContext {

	private final ValidationStrategyFactory validatorFactory;

	/*
	 * This method performs validation for specific type.
	 */
	public <T> boolean execute(ValidationType type, T input) {
		ValidationStrategy strategy = validatorFactory.findStrategy(type);
		return strategy.validate(input);
	}

	/*
	 * This method performs all validation for specific domain one by one and add
	 * the invalid one into a set if found and returns that set.
	 */
	public <T> Set<ValidationViolation> execute(ValidationDomain domain, T input) {
		Set<ValidationStrategy> validationStrategies = validatorFactory.findStrategy(domain);
		return validationStrategies.stream().filter(strategy -> !strategy.validate(input))
				.map(strategy -> new ValidationViolation(strategy.getType(), strategy.getMessage()))
				.collect(Collectors.toSet());
	}

}
