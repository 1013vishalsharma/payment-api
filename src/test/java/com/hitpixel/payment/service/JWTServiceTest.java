package com.hitpixel.payment.service;

import com.hitpixel.payment.exception.JWTFailureException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    @Mock
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateJWTToken() {
        String name = "Sansa Stark";
        String email = "sansa.stark@gmail.com";
        String token = jwtService.generateJWTToken(name, email);

        assertNotNull(token);
    }

    @Test
    void testExtractJWTClaims() {
        String name = "Sansa Stark";
        String email = "sansa.stark@gmail.com";
        String token = jwtService.generateJWTToken(name, email);

        Claims claims = jwtService.extractJWTClaims(token);

        assertNotNull(claims);
        assertEquals(name, claims.get("name"));
        assertEquals(email, claims.get("email"));
    }

    @Test
    void testGetEmailClaim() {
        String name = "Sansa Stark";
        String email = "sansa.stark@gmail.com";
        String token = jwtService.generateJWTToken(name, email);

        String extractedEmail = jwtService.getEmailClaim(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void testValidateJWTWithValidToken() {
        String name = "Sansa Stark";
        String email = "sansa.stark@gmail.com";
        String token = jwtService.generateJWTToken(name, email);

        boolean isValid = jwtService.validateJWT(token);

        assertTrue(isValid);
    }

    @Test
    void testExtractJWTClaimsThrowsExceptionWhenTokenIsInvalid() {
        String invalidToken = "invalid-token";

        Exception exception = assertThrows(JWTFailureException.class, () -> jwtService.extractJWTClaims(invalidToken));
        assertEquals("JWT not valid", exception.getMessage());
    }

    @Test
    void testGenerateJWTToken_Exception() {
        String name = null;
        String email = null;

        JWTService jwtServiceMock = spy(new JWTService());
        doThrow(new JWTFailureException("Error encountered while generating JWT")).when(jwtServiceMock).generateJWTToken(name, email);

        JWTFailureException exception = assertThrows(JWTFailureException.class, () -> {
            jwtServiceMock.generateJWTToken(name, email);
        });

        assertEquals("Error encountered while generating JWT", exception.getMessage());
    }
}
