package com.sayedbaladoh.ecommerce.validations;

import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

public interface ValidationStrategy {

	ValidationDomain getDomain();

	ValidationType getType();

	String getMessage();

	<T> boolean validate(T input);

}
