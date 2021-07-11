package com.sayedbaladoh.ecommerce.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sayedbaladoh.ecommerce.dto.common.ApiResponse;
import com.sayedbaladoh.ecommerce.dto.user.JwtAuthenticationResponse;
import com.sayedbaladoh.ecommerce.dto.user.LoginRequest;
import com.sayedbaladoh.ecommerce.dto.user.SignUpRequest;
import com.sayedbaladoh.ecommerce.service.AuthService;
import com.sayedbaladoh.ecommerce.service.impl.AuthServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Authentication Rest Controller
 * 
 * @author SayedBaladoh
 */
@Api(value = "Authentication", description = "Authentication operations APIs", tags = { "Authentication" })

@RestController
@RequestMapping("/auth")
public class AuthController {

	private AuthService authService;

	@Autowired
	public AuthController(AuthServiceImpl authService) {
		this.authService = authService;
	}

	@ApiOperation(value = "Signin user", nickname = "signin", notes = "Login user", tags = {
			"Authentication" }, response = Page.class)
	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
		return new ResponseEntity<>(authService.authenticateUser(loginRequest), HttpStatus.OK);
	}

	@ApiOperation(value = "Register a new user", nickname = "registerUser", notes = "Register a new user", tags = {
			"Authentication" }, response = Page.class)
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
		Long id = authService.registerUser(signUpRequest);
		return new ResponseEntity<>(new ApiResponse(true, "User has been saved with id: " + id), HttpStatus.CREATED);
	}
}
