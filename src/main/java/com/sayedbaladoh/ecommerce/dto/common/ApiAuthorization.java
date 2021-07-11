package com.sayedbaladoh.ecommerce.dto.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Bearer token", required = true, dataType = "string", paramType = "header"))
public @interface ApiAuthorization {
}