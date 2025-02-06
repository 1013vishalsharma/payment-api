package com.hitpixel.payment.exception;

import com.hitpixel.payment.dto.ErrorDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private UserNotFoundException userNotFoundException;

    @Mock
    private JWTFailureException jwtFailureException;

    @Mock
    private BadCredentialsException badCredentialsException;

    @Mock
    private TransactionNotExistsException transactionNotExistsException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        userNotFoundException = new UserNotFoundException("User not found");
        jwtFailureException = new JWTFailureException("JWT failure");
        badCredentialsException = new BadCredentialsException("Bad credentials");
        transactionNotExistsException = new TransactionNotExistsException("Transaction not found");
    }

    @Test
    void testUserNotFoundExceptionHandler() {
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.userNotFoundExceptionHandler(userNotFoundException, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody().message());
        assertEquals(HttpStatus.NOT_FOUND.name(), response.getBody().error());
    }

    @Test
    void testJWTFailureExceptionHandler() {
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.jwtFailureExceptionHandler(jwtFailureException, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("JWT failure", response.getBody().message());
        assertEquals(HttpStatus.UNAUTHORIZED.name(), response.getBody().error());
    }

    @Test
    void testBadCredentialsExceptionHandler() {
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.badCredentialsExceptionHandler(badCredentialsException, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", response.getBody().message());
        assertEquals(HttpStatus.UNAUTHORIZED.name(), response.getBody().error());
    }

    @Test
    void testTransactionNotExistsExceptionHandler() {
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.transactionNotExistsExceptionHandler(transactionNotExistsException, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Transaction not found", response.getBody().message());
        assertEquals(HttpStatus.NOT_FOUND.name(), response.getBody().error());
    }

    @Test
    void testDefaultExceptionHandler() {
        Exception exception = new Exception("Encountered error while processing the request");
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.defaultExceptionHandler(exception, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Encountered error while processing the request", response.getBody().message());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), response.getBody().error());
    }

    @Test
    void testMethodArgumentNotValidExceptionHandler() {
        String errorMessage = "name cannot be empty";
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        Mockito.when(bindingResult.getAllErrors()).thenReturn(
                List.of(new ObjectError("name", errorMessage), new ObjectError("email", "email not valid"))
        );
        ResponseEntity<ErrorDetails> response = globalExceptionHandler.methodArgumentNotValidExceptionHandler(methodArgumentNotValidException, webRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("name cannot be empty, email not valid", response.getBody().message());
        assertEquals(HttpStatus.BAD_REQUEST.name(), response.getBody().error());
    }
}
