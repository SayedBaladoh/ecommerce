package com.sayedbaladoh.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.exception.PaymentGetwayException;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.OrderItem;
import com.sayedbaladoh.ecommerce.service.PaymentGateway;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentService implements PaymentGateway {

	@Value("${BASE_URL}")
	private String baseURL;

	@Value("${STRIPE_SECRET_KEY}")
	private String apiKey;

	@Override
	public CheckoutSession createCheckoutSession(Order order) {

		String successURL = baseURL + "payment/success";
		String failedURL = baseURL + "payment/failed";

		Stripe.apiKey = apiKey;

		List<SessionCreateParams.LineItem> sessionItemsList = new ArrayList<SessionCreateParams.LineItem>();
		for (OrderItem checkoutItemDto : order.getOrderItems()) {
			sessionItemsList.add(createSessionLineItem(checkoutItemDto));
		}

		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.setMode(SessionCreateParams.Mode.PAYMENT).setCancelUrl(failedURL).addAllLineItem(sessionItemsList)
				.setSuccessUrl(successURL).build();

		try {
			Session session = Session.create(params);

			return CheckoutSession.builder().orderId(order.getId()).sessionId(session.getId())
					.paymentStatus(session.getPaymentStatus()).url(session.getUrl()).build();
		} catch (StripeException e) {
			throw new PaymentGetwayException(e.getMessage(), e.getCause());
		}
	}

	private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem checkoutItem) {
		return SessionCreateParams.LineItem.PriceData.builder().setCurrency("usd")
				.setUnitAmount(((long) checkoutItem.getProduct().getPrice()) * 100)
				.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
						.setName(checkoutItem.getProduct().getName()).build())
				.build();
	}

	private SessionCreateParams.LineItem createSessionLineItem(OrderItem checkoutItem) {
		return SessionCreateParams.LineItem.builder().setPriceData(createPriceData(checkoutItem))
				.setQuantity(Long.parseLong(String.valueOf(checkoutItem.getQuantity()))).build();
	}

}
