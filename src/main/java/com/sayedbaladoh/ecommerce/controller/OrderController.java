package com.sayedbaladoh.ecommerce.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sayedbaladoh.ecommerce.dto.common.ApiAuthorization;
import com.sayedbaladoh.ecommerce.dto.common.ApiPageableAuthorization;
import com.sayedbaladoh.ecommerce.dto.common.ApiResponse;
import com.sayedbaladoh.ecommerce.dto.order.CheckoutSession;
import com.sayedbaladoh.ecommerce.dto.order.OrderDto;
import com.sayedbaladoh.ecommerce.dto.order.OrderResponseDto;
import com.sayedbaladoh.ecommerce.security.CurrentUser;
import com.sayedbaladoh.ecommerce.security.UserPrincipal;
import com.sayedbaladoh.ecommerce.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Orders Rest Controller
 * 
 * @author SayedBaladoh
 */
@Api(value = "Orders", description = "Order's operations APIs", tags = { "Orders" })
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

	private final OrderService orderService;

	@ApiOperation(value = "Return paginated list of orders", nickname = "getAllOrders", notes = "Get paginated list of orders", tags = {
			"Orders" }, response = Page.class)
	@ApiPageableAuthorization
	@GetMapping(produces = { "application/json" })
	public ResponseEntity<Page<OrderResponseDto>> getOrders(@ApiIgnore Pageable pageable) {

		Page<OrderResponseDto> orders = orderService.getAllOrders(pageable);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@ApiOperation(value = "Return paginated list of orders for current user", nickname = "getUserOrders", notes = "Get paginated list of orders for current user", tags = {
			"Orders" }, response = Page.class)
	@ApiPageableAuthorization
	@GetMapping(value = "/me", produces = { "application/json" })
	public ResponseEntity<Page<OrderResponseDto>> getUserOrders(@ApiIgnore @CurrentUser UserPrincipal currentUser,
			@ApiIgnore Pageable pageable) {

		Page<OrderResponseDto> orders = orderService.getAllOrders(currentUser.getId(), pageable);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	@ApiOperation(value = "Return an order details", nickname = "getOrder", notes = "Get an order details", tags = {
			"Orders" }, response = OrderResponseDto.class)
	@ApiAuthorization
	@GetMapping(value = "/{orderId}", produces = { "application/json" })
	public ResponseEntity<OrderResponseDto> getOrder(@PathVariable("orderId") Long orderId) {

		return new ResponseEntity<>(orderService.getOrder(orderId), HttpStatus.OK);
	}

	@ApiOperation(value = "Add a new order for the current user", nickname = "addOrder", notes = "Insert a new order for the current user", tags = {
			"Orders" }, response = OrderResponseDto.class)
	@ApiAuthorization
	@PostMapping(consumes = { "application/json" })
	public ResponseEntity<ApiResponse> addOrder(@Valid @RequestBody OrderDto orderDto,
			@ApiIgnore @CurrentUser UserPrincipal currentUser) {

		OrderResponseDto order = orderService.addOrder(currentUser.getId(), orderDto);

		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/orders/{id}")
				.buildAndExpand(order.getId()).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);

		return new ResponseEntity<>(new ApiResponse(true, "Order has been saved with id: " + order.getId()),
				HttpStatus.CREATED);

	}

	@ApiOperation(value = "Checkout an order", nickname = "checkoutExistOrder", notes = "Checkout exist order", tags = {
			"Order Basket Checkout" }, response = CheckoutSession.class)
	@ApiAuthorization
	@PostMapping("/{orderId}/checkout/sessions")
	public ResponseEntity<CheckoutSession> checkoutOrder(@PathVariable("orderId") Long orderId) {

		return new ResponseEntity<>(orderService.createCheckoutSession(orderId), HttpStatus.OK);
	}

//	@ApiOperation(value = "Checkout a new order for the current user", nickname = "checkoutNewOrder", notes = "Checkout a new order for the current user", tags = {
//			"Order Basket Checkout" }, response = CheckoutSession.class)
//	@ApiAuthorization
//	@PostMapping(value = "/checkout/sessions", consumes = { "application/json" })
//	public ResponseEntity<CheckoutSession> checkoutOrder(@Valid @RequestBody OrderDto checkoutOrderDto,
//			@ApiIgnore @CurrentUser UserPrincipal currentUser) {
//
//		return new ResponseEntity<>(orderService.createCheckoutSession(currentUser.getId(), checkoutOrderDto),
//				HttpStatus.OK);
//	}

}
