package com.MNS.global_shipping_platform.exception;

import com.MNS.global_shipping_platform.exception.auth.UserEmailAlreadyExists;
import com.MNS.global_shipping_platform.exception.booking.BookingAlreadyAssignedException;
import com.MNS.global_shipping_platform.exception.booking.BookingNotFoundException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingException;
import com.MNS.global_shipping_platform.exception.booking.InvalidBookingStatusException;
import com.MNS.global_shipping_platform.exception.container.ContainerAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.container.ContainerNotFoundException;
import com.MNS.global_shipping_platform.exception.container.InvalidContainerStatusException;
import com.MNS.global_shipping_platform.exception.customer.CustomerAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.customer.CustomerNotFoundException;
import com.MNS.global_shipping_platform.exception.port.PortAlreadyExistExceptions;
import com.MNS.global_shipping_platform.exception.port.PortNotFoundException;
import com.MNS.global_shipping_platform.exception.portCalls.InvalidPortCallException;
import com.MNS.global_shipping_platform.exception.sailing.InvalidSailingScheduleException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNotFoundException;
import com.MNS.global_shipping_platform.exception.sailing.SailingNumberAlreadyExists;
import com.MNS.global_shipping_platform.exception.vessel.VesselAlreadyExistsException;
import com.MNS.global_shipping_platform.exception.vessel.VesselNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(response);
    }

    // --------------------------------------------------
    // 404 - Resource not found
    // --------------------------------------------------

    @ExceptionHandler({
            BookingNotFoundException.class,
            ContainerNotFoundException.class,
            CustomerNotFoundException.class,
            PortNotFoundException.class,
            SailingNotFoundException.class,
            VesselNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    // --------------------------------------------------
    // 409 - Duplicate resources / state conflicts
    // --------------------------------------------------

    @ExceptionHandler({
            BookingAlreadyAssignedException.class,
            ContainerAlreadyExistsException.class,
            CustomerAlreadyExistsException.class,
            InvalidBookingStatusException.class,
            InvalidContainerStatusException.class,
            InvalidSailingScheduleException.class,
            PortAlreadyExistExceptions.class,
            SailingNumberAlreadyExists.class,
            UserEmailAlreadyExists.class,
            VesselAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    // --------------------------------------------------
    // 400 - Invalid booking request / business input
    // --------------------------------------------------

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBooking(
            InvalidBookingException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    // --------------------------------------------------
    // 400 - @Valid request body validation
    // --------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    // --------------------------------------------------
    // 400 - Invalid JSON / unreadable request body
    // --------------------------------------------------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableRequest(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is invalid or contains an unsupported value.",
                request
        );
    }

    // --------------------------------------------------
    // 400 - Constraint validation failures
    // --------------------------------------------------

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(violation ->
                        violation.getPropertyPath()
                                + ": "
                                + violation.getMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    // --------------------------------------------------
    // 401 - Authentication failure inside controller flow
    // --------------------------------------------------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailure(
            AuthenticationException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password.",
                request
        );
    }

    // --------------------------------------------------
    // 403 - Authenticated user lacks permission
    // --------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request
        );
    }

    // --------------------------------------------------
    // 409 - Database constraint violation
    // --------------------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn(
                "Database constraint violation on {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "The request conflicts with existing data.",
                request
        );
    }

    // --------------------------------------------------
    // 500 - Unexpected errors
    // --------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected error while handling {} {}",
                request.getMethod(),
                request.getRequestURI(),
                ex
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                request
        );
    }

    @ExceptionHandler(InvalidPortCallException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPortCall(
            InvalidPortCallException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }
}