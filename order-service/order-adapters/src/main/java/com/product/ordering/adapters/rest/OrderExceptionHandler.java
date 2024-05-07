package com.product.ordering.adapters.rest;


import com.product.ordering.domain.exception.OrderDomainException;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.system.application.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class OrderExceptionHandler {

    @ExceptionHandler(value = {OrderDomainException.class})
    ResponseEntity<ErrorResponse> handleException(OrderDomainException orderDomainException) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .responseCode(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(orderDomainException.getMessage())
                        .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {OrderNotFoundException.class})
    ResponseEntity<ErrorResponse> handleException(OrderNotFoundException orderNotFoundException) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .responseCode(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(orderNotFoundException.getMessage())
                        .build(), HttpStatus.NOT_FOUND);
    }
}
