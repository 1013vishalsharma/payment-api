package com.hitpixel.payment.exception;

import com.hitpixel.payment.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Method to handle UserNotFoundException
     * @param exception UserNotFoundException
     * @param webRequest webRequest
     * @return Error details object for response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> userNotFoundExceptionHandler(UserNotFoundException exception, WebRequest webRequest) {
        log.error("Error encountered while fetching user", exception);
        return new ResponseEntity<>(new ErrorDetails(
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.name()
        ), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle JWTFailureException
     * @param exception JWTFailureException
     * @param webRequest webRequest
     * @return Error details object for response
     */
    @ExceptionHandler(JWTFailureException.class)
    public ResponseEntity<ErrorDetails> jwtFailureExceptionHandler(JWTFailureException exception, WebRequest webRequest) {
        log.error("Error encountered while processing JWT", exception);
        return new ResponseEntity<>(new ErrorDetails(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.name()
        ), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to handle BadCredentialsException
     * @param exception BadCredentialsException
     * @param webRequest webRequest
     * @return Error details object for response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> badCredentialsExceptionHandler(BadCredentialsException exception, WebRequest webRequest) {
        log.error("Error encountered while authenticating user", exception);
        return new ResponseEntity<>(new ErrorDetails(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.name()
        ), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to handle TransactionNotExistsException
     * @param exception TransactionNotExistsException
     * @param webRequest webRequest
     * @return Error deatils object for response
     */
    @ExceptionHandler(TransactionNotExistsException.class)
    public ResponseEntity<ErrorDetails> transactionNotExistsExceptionHandler(TransactionNotExistsException exception, WebRequest webRequest) {
        log.error("Error encountered while retrieving transactions from the system", exception);
        return new ResponseEntity<>(new ErrorDetails(
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.name()
        ), HttpStatus.NOT_FOUND);
    }


    /**
     * Method to handle MethodArgumentNotValidException
     * @param exception MethodArgumentNotValidException
     * @param webRequest webRequest
     * @return Error details object for response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception, WebRequest webRequest) {
        log.error("Request body is not valid", exception);
        String errorMessage = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorDetails(
                errorMessage,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.name()
        ), HttpStatus.BAD_REQUEST);
    }

    /**
     * Method to handle any Exception
     * @param exception Exception
     * @param webRequest webRequest
     * @return Error details object for response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> defaultExceptionHandler(Exception exception, WebRequest webRequest) {
        log.error("Error encountered while processing the request", exception);
        return new ResponseEntity<>(new ErrorDetails(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
