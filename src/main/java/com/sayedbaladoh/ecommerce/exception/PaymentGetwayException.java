package com.sayedbaladoh.ecommerce.exception;

public class PaymentGetwayException extends RuntimeException {

	private static final long serialVersionUID = 1591310537366275145L;

	public PaymentGetwayException() {
		super();
	}

	public PaymentGetwayException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PaymentGetwayException(final String message) {
		super(message);
	}

	public PaymentGetwayException(final Throwable cause) {
		super(cause);
	}
}