package com.sayedbaladoh.ecommerce.validations.policy;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sayedbaladoh.ecommerce.dto.order.OrderDto;
import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemDto;
import com.sayedbaladoh.ecommerce.service.ProductService;
import com.sayedbaladoh.ecommerce.validations.ValidationStrategy;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationDomain;
import com.sayedbaladoh.ecommerce.validations.enums.ValidationType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductsExistenceValidator implements ValidationStrategy {

	private final ProductService productService;

	@Value("${constraints.product.existence.message}")
	private String messagePreFix;

	private String message;

	@Override
	public ValidationDomain getDomain() {
		return ValidationDomain.ORDER_SAVE;
	}

	@Override
	public ValidationType getType() {
		return ValidationType.Products_Existence;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public <T> boolean validate(T o) {
		List<OrderItemDto> list = ((OrderDto) o).getOrderItems().stream()
				.filter(op -> productService.getProduct(op.getProduct().getId()).isEmpty())
				.collect(Collectors.toList());
		message = messagePreFix + list.stream().map(item -> "#" + item.getProduct().getId())
				.collect(Collectors.joining(", ", "{", "}"));
		return list.isEmpty();
	}
}
