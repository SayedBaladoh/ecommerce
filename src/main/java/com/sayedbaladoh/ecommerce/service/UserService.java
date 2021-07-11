package com.sayedbaladoh.ecommerce.service;

import com.sayedbaladoh.ecommerce.dto.user.UserSummary;
import com.sayedbaladoh.ecommerce.security.UserPrincipal;

public interface UserService {

	UserSummary getCurrentUser(UserPrincipal userPrincipal);
}
