package com.sayedbaladoh.ecommerce.service;

import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.model.Order;

public interface PaymentGateway {

	CheckoutSession createCheckoutSession(Order order);
}
