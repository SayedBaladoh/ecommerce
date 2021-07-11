package com.sayedbaladoh.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sayedbaladoh.ecommerce.dto.common.ApiAuthorization;
import com.sayedbaladoh.ecommerce.dto.user.UserSummary;
import com.sayedbaladoh.ecommerce.security.CurrentUser;
import com.sayedbaladoh.ecommerce.security.UserPrincipal;
import com.sayedbaladoh.ecommerce.service.impl.UserServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * User Rest Controller
 * 
 * @author SayedBaladoh
 */
@Api(value = "Users", description = "User's operations APIs", tags = { "Users" })

@RestController
@RequestMapping("/users")
public class UserController {

	private UserServiceImpl userService;

	@Autowired
	public UserController(UserServiceImpl userService) {
		this.userService = userService;
	}

	@ApiOperation(value = "Return current user details", nickname = "getCurrentUser", notes = "Get current user details", tags = {
			"Users" }, response = Page.class)
	@PreAuthorize("hasRole('USER')")
	@ApiAuthorization
	@GetMapping("me")
	public UserSummary getCurrentUser(@ApiIgnore @CurrentUser UserPrincipal currentUser) {
		return userService.getCurrentUser(currentUser);
	}

}
