package com.MNS.global_shipping_platform.exception;

import com.MNS.global_shipping_platform.customer.CustomerAlreadyExistsException;
import com.MNS.global_shipping_platform.customer.CustomerNotFoundException;
import com.MNS.global_shipping_platform.exception.port.PortAlreadyExistExceptions;
import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNotFoundException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNumberAlreadyExists;
import com.MNS.global_shipping_platform.exception.vessel.VesselAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.vessel.VesselNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status,Exception ex,HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCustomerAlreadyExists(
            CustomerAlreadyExistsException ex,
            HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.CONFLICT,ex,request);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(HttpStatus.NOT_FOUND,ex,request);
    }

    @ExceptionHandler(PortNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePortNotFound(PortNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,ex,request);
    }

    @ExceptionHandler(PortAlreadyExistExceptions.class)
    public ResponseEntity<ErrorResponse> handlePortAlreadyExists(PortAlreadyExistExceptions ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,ex,request);
    }

    @ExceptionHandler(VesselAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleVesselAlreadyExists(VesselAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,ex,request);
    }

    @ExceptionHandler(VesselNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVesselNotFound(VesselNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,ex,request);
    }

    @ExceptionHandler(SailingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSailingNotFound(SailingNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,ex,request);
    }

    @ExceptionHandler(SailingNumberAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleSailingNumberAlreadyExists(SailingNumberAlreadyExists ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,ex,request);
    }

    @ExceptionHandler(InvalidSailingScheduleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSailingSchedule(InvalidSailingScheduleException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT,ex,request);
    }

}
