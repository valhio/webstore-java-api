package com.github.valhio.storeapi.exception;

import com.github.valhio.storeapi.domain.HttpResponse;
import com.github.valhio.storeapi.exception.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Date;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

/*
    The ExceptionHandling class is responsible for handling all exceptions thrown by the application, as long as they extend the ExceptionHandler class.

    This class is a controller advice that provides a centralized way of handling exceptions thrown by the application.
    It defines various exception handler methods that handle specific exceptions thrown by the application.
    Each exception handler method maps an exception to an appropriate HTTP response status and returns an HTTP response
    with a custom message.
*/
@Slf4j
@RestControllerAdvice
public class ExceptionHandling {
    public static final String ERROR_PATH = "/error";
    private static final String DEFAULT_ERROR_MESSAGE = "An error occurred while processing the request.";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_CAUSE = "An error occurred while processing the request. Caused by: ";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_CAUSE_AND_MESSAGE = "An error occurred while processing the request. Caused by: %s. Message: %s";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_MESSAGE = "An error occurred while processing the request. Message: %s";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_CAUSE_AND_MESSAGE_AND_STACKTRACE = "An error occurred while processing the request. Caused by: %s. Message: %s. Stacktrace: %s";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_MESSAGE_AND_STACKTRACE = "An error occurred while processing the request. Message: %s. Stacktrace: %s";
    private static final String DEFAULT_ERROR_MESSAGE_WITH_STACKTRACE = "An error occurred while processing the request. Stacktrace: %s";
    private static final String ACCOUNT_LOCKED = "Your account has been locked. Please contact administration.";
    private static final String METHOD_IS_NOT_ALLOWED = "This request method is not allowed on this endpoint. Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    private static final String INCORRECT_CREDENTIALS = "Username / password incorrect. Please try again";
    private static final String ACCOUNT_DISABLED = "Your account has been disabled. If this is an error, please contact administration.";
    private static final String ERROR_PROCESSING_FILE = "Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION = "You do not have enough permission";
    private static final String EMAIL_SEND_ERROR = "There was an error sending email";


//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
//        return createHttpResponse(BAD_REQUEST, String.format("There is no mapping for this URL %s", e.getRequestURL()));
//    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<HttpResponse> productNotFoundException(ProductNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<HttpResponse> passwordNotMatchException(PasswordNotMatchException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HttpResponse> usernameNotFoundException(UsernameNotFoundException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException e) {
        return createHttpResponse(UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<HttpResponse> orderNotFoundException(OrderNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(OrderItemNotFoundException.class)
    public ResponseEntity<HttpResponse> orderItemNotFoundException(OrderItemNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<HttpResponse> invoiceNotFoundException(InvoiceNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ProductReviewNotFoundException.class)
    public ResponseEntity<HttpResponse> productReviewNotFoundException(ProductReviewNotFoundException e) {
        return createHttpResponse(NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    // This exception is thrown when a required part of a multipart request is missing.
    public ResponseEntity<HttpResponse> missingServletRequestPartException(MissingServletRequestPartException e) {
        return createHttpResponse(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next(); // get the first supported method
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
        HttpResponse build = HttpResponse.builder()
                .statusCode(httpStatus.value())
                .status(httpStatus)
                .reason(httpStatus.getReasonPhrase().toUpperCase())
                .message(message)
                .timeStamp(new Date())
                .build();

        return new ResponseEntity<>(build, httpStatus);
    }

    //     Default exception handler
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
//        log.error(exception.getMessage());
//        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
//    }

    //    @ExceptionHandler(IOException.class)
//    public ResponseEntity<HttpResponse> iOException(IOException exception) {
//        log.error(exception.getMessage());
//        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
//    }
}
