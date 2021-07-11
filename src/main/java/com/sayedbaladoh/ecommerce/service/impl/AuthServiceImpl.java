package com.sayedbaladoh.ecommerce.service.impl;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.user.JwtAuthenticationResponse;
import com.sayedbaladoh.ecommerce.dto.user.LoginRequest;
import com.sayedbaladoh.ecommerce.dto.user.SignUpRequest;
import com.sayedbaladoh.ecommerce.enums.RoleName;
import com.sayedbaladoh.ecommerce.exception.AppException;
import com.sayedbaladoh.ecommerce.exception.ConflictException;
import com.sayedbaladoh.ecommerce.model.Role;
import com.sayedbaladoh.ecommerce.model.User;
import com.sayedbaladoh.ecommerce.repository.RoleRepository;
import com.sayedbaladoh.ecommerce.repository.UserRepository;
import com.sayedbaladoh.ecommerce.security.JwtTokenProvider;
import com.sayedbaladoh.ecommerce.security.UserPrincipal;
import com.sayedbaladoh.ecommerce.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;

	@Override
	public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);

		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		log.info("User with [email: {}] has logged in", userPrincipal.getEmail());

		return new JwtAuthenticationResponse(jwt);
	}

	@Override
	public Long registerUser(SignUpRequest signUpRequest) {

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new ConflictException("Email [email: " + signUpRequest.getEmail() + "] is already taken");
		}

		User user = new User(signUpRequest.getName(), signUpRequest.getEmail(), signUpRequest.getPassword());

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AppException("User Role not set. Add default roles to database."));

		user.setRoles(Collections.singleton(userRole));

		log.info("Successfully registered user with [email: {}]", user.getEmail());

		return userRepository.save(user).getId();
	}
}
