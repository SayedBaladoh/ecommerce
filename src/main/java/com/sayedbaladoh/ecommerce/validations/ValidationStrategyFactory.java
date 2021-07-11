package com.sayedbaladoh.ecommerce.validations;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

@Component
public class ValidationStrategyFactory {

	private Map<ValidationType, ValidationStrategy> validationTypeStrategies;
	private Map<ValidationDomain, Set<ValidationStrategy>> validationDomainStrategies;

	@Autowired
	public ValidationStrategyFactory(Set<ValidationStrategy> validationStrategies) {
		createStrategy(validationStrategies);
	}

	public ValidationStrategy findStrategy(ValidationType type) {
		return validationTypeStrategies.get(type);
	}

	public Set<ValidationStrategy> findStrategy(ValidationDomain domain) {
		return validationDomainStrategies.get(domain);
	}

	private void createStrategy(Set<ValidationStrategy> validationSet) {
		validationTypeStrategies = validationSet.stream()
				.collect(Collectors.toMap(ValidationStrategy::getType, Function.identity()));

		validationDomainStrategies = validationSet.stream().collect(Collectors.groupingBy(ValidationStrategy::getDomain,
				Collectors.mapping(Function.identity(), Collectors.toSet())));
	}

}
