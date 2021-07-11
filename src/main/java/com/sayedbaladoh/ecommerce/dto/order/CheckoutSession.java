package com.sayedbaladoh.ecommerce.dto.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckoutSession {
	private long orderId;
	private String sessionId;
	private String paymentStatus;
	private String url;
}
