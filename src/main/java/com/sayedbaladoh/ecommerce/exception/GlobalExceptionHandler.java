package com.sayedbaladoh.ecommerce.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sayedbaladoh.ecommerce.dto.common.ErrorItem;
import com.sayedbaladoh.ecommerce.dto.common.ErrorResponse;
import com.sayedbaladoh.ecommerce.validations.ValidationViolation;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler({ BadRequestException.class, NoSuchFieldException.class, NumberFormatException.class,
			JsonProcessingException.class, IllegalArgumentException.class, PropertyReferenceException.class })
	public ResponseEntity<ErrorItem> runtime(RuntimeException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorItem> handle(ResourceNotFoundException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorItem> handle(BadCredentialsException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ErrorItem> handle(ConflictException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ErrorItem> handle(HttpClientErrorException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(PaymentGetwayException.class)
	public ResponseEntity<ErrorItem> handle(PaymentGetwayException e) {
		log.info(e.getMessage());
		ErrorItem error = new ErrorItem();
		error.setMessage(e.getMessage());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@SuppressWarnings("rawtypes")
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
		ErrorResponse errors = new ErrorResponse();
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getMessageTemplate());
			error.setMessage(violation.getMessage());
			errors.addError(error);
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
		ErrorResponse errors = new ErrorResponse();
		e.getBindingResult().getAllErrors().forEach((err) -> {
			ErrorItem error = new ErrorItem();
			error.setCode(((FieldError) err).getField());
			error.setMessage(err.getDefaultMessage());
			errors.addError(error);
		});

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationViolationException.class)
	public ResponseEntity<ErrorResponse> handle(ValidationViolationException e) {
		ErrorResponse errors = new ErrorResponse();
		for (ValidationViolation violation : e.getConstraintViolations()) {
			ErrorItem error = new ErrorItem();
			error.setCode(violation.getType().toString());
			error.setMessage(violation.getMessage());
			errors.addError(error);
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
}
