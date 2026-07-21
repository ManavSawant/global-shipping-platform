package com.MNS.global_shipping_platform.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<CustomerErrorResponse> handleCustomerAlreadyExistsException(CustomerAlreadyExistsException e,
                                                                                      HttpServletRequest request) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(customerErrorResponse);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<CustomerErrorResponse> handleException(CustomerNotFoundException e, HttpServletRequest request) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(customerErrorResponse);
    }

}
