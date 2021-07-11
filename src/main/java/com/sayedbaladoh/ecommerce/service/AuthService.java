package com.sayedbaladoh.ecommerce.service;

import com.sayedbaladoh.ecommerce.dto.user.JwtAuthenticationResponse;
import com.sayedbaladoh.ecommerce.dto.user.LoginRequest;
import com.sayedbaladoh.ecommerce.dto.user.SignUpRequest;

public interface AuthService {

	JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);

	Long registerUser(SignUpRequest signUpRequest);
}
