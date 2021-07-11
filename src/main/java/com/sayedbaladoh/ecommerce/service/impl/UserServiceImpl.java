package com.sayedbaladoh.ecommerce.service.impl;

import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.user.UserSummary;
import com.sayedbaladoh.ecommerce.security.UserPrincipal;
import com.sayedbaladoh.ecommerce.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Override
    public UserSummary getCurrentUser(UserPrincipal userPrincipal) {
        return UserSummary.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .name(userPrincipal.getName())
                .build();
    }
}
